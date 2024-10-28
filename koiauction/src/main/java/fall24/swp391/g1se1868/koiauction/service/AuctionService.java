package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.model.auction.AuctionDetailDTO;
import fall24.swp391.g1se1868.koiauction.model.auction.KoiAuctionResponseDTO;
import fall24.swp391.g1se1868.koiauction.model.auction.KoiFishAuctionAll;
import fall24.swp391.g1se1868.koiauction.model.auction.KoiInfo;
import fall24.swp391.g1se1868.koiauction.model.koifishdto.KoiDataDTO;
import fall24.swp391.g1se1868.koiauction.model.koifishdto.KoiMediaDTO;
import fall24.swp391.g1se1868.koiauction.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuctionService {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private AuctionKoiRepository auctionKoiRepository;

    @Autowired
    private AuctionParticipantRepository auctionParticipantRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    @Lazy
    private BidService bidService;

    @Autowired
    private KoiMediaRepository koiMediaRepository;

    @Autowired
    AuctionSchedulerService auctionSchedulerService;

    @Autowired
    SystemConfigService systemConfigService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    WalletService walletService;

    public Auction getAuctionById(Integer auctionId) {
        return auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found with ID: " + auctionId));
    }

    public AuctionDetailDTO getAuctionWithKoiByID(Integer id) {
        Optional<Auction> auctionOptional = auctionRepository.findById(id);
        if (auctionOptional.isPresent()) {
            Auction auction = auctionOptional.get();
            return convertToAuctionDetailDTO(auction);
        } else {
            throw new RuntimeException("Auction not found with id: " + id);
        }
    }



    public List<Map<String, Object>> getPastAuctionsWithWinnerName() {
        List<Object[]> results = auctionRepository.findPastAuctionsWithWinnerName();
        List<Map<String, Object>> pastAuctions = new ArrayList<>();
        for (Object[] result : results) {
            Auction auction = (Auction) result[0];
            String winnerName = (String) result[1];
            Map<String, Object> auctionData = new HashMap<>();
            auctionData.put("auction", auction);
            auctionData.put("winnerName", winnerName);
            pastAuctions.add(auctionData);
        }

        return pastAuctions;
    }


    public AuctionDetailDTO getAuctionsParticipantByUser(int userId) {
        List<Integer> auctionIds = auctionParticipantRepository.findAuctionIdsByUserId(userId);
        if (!auctionIds.isEmpty()) {
            // Giả sử bạn chỉ muốn lấy một phiên đấu giá đầu tiên từ danh sách
            Integer auctionId = auctionIds.get(0);
            return getAuctionWithKoiByID(auctionId); // Gọi phương thức đã sửa để lấy thông tin chi tiết
        } else {
            throw new RuntimeException("No auctions found for user ID: " + userId);
        }
    }


    public Page<KoiAuctionResponseDTO> getWinnerAuctionByWinnerID(int winnerID, Pageable pageable) {
        // Lấy danh sách các phiên đấu giá đã thắng với phân trang
        Page<Auction> auctionPage = auctionRepository.getAuctionbyWinnerID(winnerID, pageable);

        // Chuyển đổi từ Page<Auction> sang Page<KoiAuctionResponseDTO>
        return getAuctionDetails(auctionPage);
    }


    public boolean isUserParticipantForAuction(int userId, int auctionId) {
        return auctionParticipantRepository.existsByUserIdAndAuctionId(userId, auctionId);
    }
    @Transactional
    public String addAuction(AuctionRequest request,int breerderID) {
        Auction auction = new Auction();
        auction.setBreederID(breerderID);
        auction.setAuctionMethod(request.getAuctionMethod());
        auction.setStartTime(request.getStartTime());
        auction.setEndTime(request.getEndTime());
        auction.setStartingPrice(request.getStartingPrice());
        auction.setBuyoutPrice(request.getBuyoutPrice());
        auction.setBidderDeposit(request.getBidderDeposit());
        auction.setBreederDeposit(Math.round(request.getStartingPrice() * systemConfigService.getBreederDeposit()));
        auction.setAuctionFee(systemConfigService.getAuctionFee().longValue());
        auction.setBidStep(request.getBidStep());
        auction.setStatus("Pending");
        auction.setCreateAt(Instant.now());
        walletService.deposit(breerderID,Math.round(request.getStartingPrice() * systemConfigService.getBreederDeposit()));
        Auction savedAuction = auctionRepository.save(auction);
        if(savedAuction != null) {
            try {
                Instant startTime = auction.getStartTime();
                Instant endTime = auction.getEndTime();
                auctionSchedulerService.scheduleStartAuction(auction.getId(), startTime);
                auctionSchedulerService.scheduleCloseAuction(auction.getId(), endTime);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        for (Integer koiId : request.getKoiIds()) {
            AuctionKoi auctionKoi = new AuctionKoi();
            AuctionKoiId auctionKoiId = new AuctionKoiId();
            auctionKoiId.setAuctionID(savedAuction.getId());
            auctionKoiId.setKoiID(koiId);
            auctionKoi.setId(auctionKoiId);
            auctionKoi.setAuctionID(savedAuction);
            auctionKoi.setKoiID(new KoiFish(koiId));
            auctionKoiRepository.save(auctionKoi);
        }
        return savedAuction!=null?"Add Auction Successfully":"Add Auction Failed";
    }
    public Page<KoiAuctionResponseDTO> getAllActionRequest(Pageable pageable) {
        Page<Auction> auctionPage = auctionRepository.getAllAuctionRequest(pageable);
        return getAuctionDetails(auctionPage);
    }

    public Auction approveAuction(Integer auctionId, Integer UserID) {
        Auction auction = auctionRepository.getById(auctionId);
        if (!auction.getStatus().equals("Pending")) {
            throw new IllegalArgumentException("Auction must be in Pending status to be approved.");
        }
        auction.setStatus("Scheduled");
        auction.setStaffID(UserID);
        return auctionRepository.save(auction);
    }
    @Transactional
    public Auction rejectAuction(Integer auctionId, Integer UserID) {
        Auction auction = auctionRepository.getById(auctionId);
        if(auction==null){
            throw  new EntityNotFoundException("Auction not found");
        }
        if (!auction.getStatus().equals("Pending")) {
            throw new IllegalArgumentException("Auction must be in Pending status to be approved.");
        }
        auction.setStatus("Reject");
        auction.setStaffID(UserID);
        walletService.refundDeposit(auction.getBreederID(),auction.getBreederDeposit());
        return auctionRepository.save(auction);
    }
    public void updateAuctionStatusOngoing() {
        List<Auction> auctions = auctionRepository.findAll();
        ZonedDateTime nowZoned = ZonedDateTime.now(ZoneId.systemDefault());
        for (Auction auction : auctions) {
            if (auction.getStartTime().isBefore(nowZoned.toInstant()) && auction.getStatus().equals("Scheduled")) {
                auction.setStatus("Ongoing");
                auctionRepository.save(auction);
            }
        }
    }
    public void updateAuctionStatusFinished(Integer auctionId) {
            Auction auction = auctionRepository.getById(auctionId);
            if (auction.getStatus().equals("Closed")) {
                auction.setStatus("Finished");
                auctionRepository.save(auction);
        }
    }
    public Integer determineWinner(Auction auction) {
        List<Bid> bids = bidRepository.findByAuctionID(auction.getId());
        if (bids.isEmpty()) {
            System.out.println("No bids found for auction: " + auction.getId());
            return null;
        }
        Bid highestBid = bids.stream()
                .max((b1, b2) -> Long.compare(b1.getAmount(), b2.getAmount()))
                .orElse(null);
        if (highestBid != null) {
            auction.setWinnerID(highestBid.getBidderID().getId());
            System.out.println("Winner determined for auction " + auction.getId() + ": " + highestBid.getBidderID().getFullName());
            return highestBid.getBidderID().getId();
        }
        return null;
    }

    private void returnDepositsToLosers(Auction auction) {
        // Logic for returning deposits
        System.out.println("Deposits returned for auction: " + auction.getId());
    }

    // Dummy method for sending payment requests to the winner
    private void requestPaymentFromWinner(Auction auction) {

        System.out.println("Payment request sent to winner for auction: " + auction.getId());
    }

    // Logic to handle other auction end tasks like returning deposits, requiring payments, etc.
    public void processEndOfAuctionTasks(Auction auction) {
        returnDepositsToLosers(auction);
        requestPaymentFromWinner(auction);
        System.out.println("End-of-auction tasks completed for auction " + auction.getId());
    }

    public void closeAuction(Auction auction) {
        auction.setStatus("Closed");
        Integer winnerID = determineWinner(auction);
        if (winnerID != null) {
            auction.setWinnerID(winnerID);
            long finalPrice = bidService.getCurrentPrice(auction.getId());
            auction.setFinalPrice(finalPrice);
            messagingTemplate.convertAndSend("/topic/auction/" + auction.getId(),
                    new AuctionNotification("winner", winnerID, "Chúc mừng! Bạn đã chiến thắng cuộc đấu giá với giá " + finalPrice + "!"));
        }
        List<Bid> bids = bidService.getAllBidsForAuction(auction.getId());
        for (Bid bid : bids) {
            int bidderID = bid.getBidderID().getId();
            if (bidderID != winnerID) {
                messagingTemplate.convertAndSend("/topic/auction/" + auction.getId(),
                        new AuctionNotification("loser", bidderID, "Cuộc đấu giá đã kết thúc. Bạn đã không thắng cuộc đấu giá này."));
            }
        }
        messagingTemplate.convertAndSend("/topic/auction/" + auction.getId(),
                new AuctionNotification("status", null, "Đấu giá đã kết thúc."));
        processEndOfAuctionTasks(auction);
        auctionRepository.save(auction);
    }

    private AuctionDetailDTO convertToAuctionDetailDTO(Auction auction) {
        AuctionDetailDTO auctionDTO = new AuctionDetailDTO();
        auctionDTO.setId(auction.getId());
        auctionDTO.setBreederID(auction.getBreederID());

        // Lấy thông tin Breeder theo BreederID
        User breeder = userRepository.findById(auction.getBreederID())
                .orElseThrow(() -> new RuntimeException("Breeder not found with id: " + auction.getBreederID()));
        auctionDTO.setBreederFullName(breeder.getFullName());

        auctionDTO.setStaffID(auction.getStaffID());
        auctionDTO.setWinnerID(auction.getWinnerID());
        auctionDTO.setAuctionMethod(auction.getAuctionMethod());
        auctionDTO.setStartTime(auction.getStartTime());
        auctionDTO.setEndTime(auction.getEndTime());
        auctionDTO.setBreederDeposit(auction.getBreederDeposit());
        auctionDTO.setBidderDeposit(auction.getBidderDeposit());
        auctionDTO.setStartingPrice(auction.getStartingPrice());
        auctionDTO.setBuyoutPrice(auction.getBuyoutPrice());
        auctionDTO.setFinalPrice(auction.getFinalPrice());
        auctionDTO.setBidStep(auction.getBidStep());
        auctionDTO.setAuctionFee(auction.getAuctionFee());
        auctionDTO.setCreateAt(auction.getCreateAt());
        auctionDTO.setStatus(auction.getStatus());

        // Lấy danh sách KoiFish và media tương ứng
        List<KoiFish> koiFishList = auctionKoiRepository.findKoiFishByAuctionId(auction.getId());
        List<KoiDataDTO> koiDataList = new ArrayList<>();

        for (KoiFish koiFish : koiFishList) {
            KoiDataDTO koiDataDTO = new KoiDataDTO();
            koiDataDTO.setId(koiFish.getId());
            koiDataDTO.setCountry(koiFish.getCountryID().getCountry()); // Lấy tên quốc gia
            koiDataDTO.setKoiType(koiFish.getKoiTypeID().getTypeName()); // Lấy loại cá
            koiDataDTO.setWeight(koiFish.getWeight());
            koiDataDTO.setSex(koiFish.getSex());
            koiDataDTO.setBirthday(koiFish.getBirthday());
            koiDataDTO.setDescription(koiFish.getDescription());
            koiDataDTO.setLength(koiFish.getLength());
            koiDataDTO.setStatus(koiFish.getStatus());
            koiDataDTO.setKoiName(koiFish.getKoiName());

            // Lấy danh sách media cho từng KoiFish
            List<KoiMedia> koiMediaList = koiMediaRepository.findByKoiID(koiFish);
            List<KoiMediaDTO> koiMediaDTOs = koiMediaList.stream()
                    .map(media -> new KoiMediaDTO(media.getMediaType(),media.getUrl()))
                    .collect(Collectors.toList());
            koiDataDTO.setKoiMedia(koiMediaDTOs);

            koiDataList.add(koiDataDTO);
        }

        auctionDTO.setKoiData(koiDataList);
        return auctionDTO;
    }


            public Page<KoiAuctionResponseDTO> getAuctionDetails(Page<Auction> auctionPage) {
                List<KoiAuctionResponseDTO> responseList = new ArrayList<>();

                for (Auction auction : auctionPage.getContent()) {
                    List<Integer> koiFishIds = auctionKoiRepository.findKoiFishByAuctionId(auction.getId())
                            .stream()
                            .map(KoiFish::getId)
                            .collect(Collectors.toList());

                    KoiAuctionResponseDTO response = new KoiAuctionResponseDTO(auction, koiFishIds);
                    responseList.add(response);
                }

                // Trả về đối tượng Page chứa danh sách DTO
                return new PageImpl<>(responseList, auctionPage.getPageable(), auctionPage.getTotalElements());
            }
    public List<KoiAuctionResponseDTO> getAuctionDetails(List<Auction> auctionPage) {
        List<KoiAuctionResponseDTO> responseList = new ArrayList<>();

        for (Auction auction : auctionPage) {
            List<Integer> koiFishIds = auctionKoiRepository.findKoiFishByAuctionId(auction.getId())
                    .stream()
                    .map(KoiFish::getId)
                    .collect(Collectors.toList());

            KoiAuctionResponseDTO response = new KoiAuctionResponseDTO(auction, koiFishIds);
            responseList.add(response);
        }

        // Trả về đối tượng Page chứa danh sách DTO
        return responseList;
    }

    public Page<KoiFishAuctionAll> getAllAuction(Page<Auction> auctionPage) {
        List<KoiFishAuctionAll> responseList = new ArrayList<>();

        for (Auction auction : auctionPage.getContent()) {
            // Lấy danh sách cá Koi liên quan đến phiên đấu giá
            List<KoiFish> koiFishList = auctionKoiRepository.findKoiFishByAuctionId(auction.getId());

            // Nếu không có KoiFish nào thì bỏ qua phiên đấu giá này
            if (koiFishList.isEmpty()) {
                continue; // Hoặc bạn có thể xử lý theo cách khác
            }

            // Tạo danh sách thông tin cá Koi
            List<KoiInfo> koiInfoList = new ArrayList<>();
            for (KoiFish koiFish : koiFishList) {
                // Lấy hình ảnh cho cá Koi
                KoiMedia headerImageMedia = koiMediaRepository.findByKoiIDAndMediaType(koiFish, "Header Image").orElse(null);
                String headerImageUrl = headerImageMedia != null ? headerImageMedia.getUrl() : null;

                // Tạo đối tượng KoiInfo và thêm vào danh sách
                KoiInfo koiInfo = new KoiInfo(
                        koiFish.getId(), // ID cá Koi
                        koiFish.getKoiName(), // Tên cá Koi
                        headerImageUrl // Hình ảnh cá Koi
                );
                koiInfoList.add(koiInfo);
            }

            // Tạo đối tượng KoiFishAuctionAll với danh sách thông tin cá Koi
            KoiFishAuctionAll response = new KoiFishAuctionAll(
                    auction.getId(), // ID phiên đấu giá
                    auction.getStartingPrice(), // Danh sách thông tin cá Koi
                    auction.getStatus(), // Giá khởi điểm
                    auction.getFinalPrice(), // Giá mua đứt
                    auction.getStartTime(), // Giá cuối cùng
                    auction.getEndTime(),
                    auction.getAuctionMethod(),// Bước giá
                    koiInfoList // Thời gian kết thúc
            );

            responseList.add(response); // Thêm đối tượng vào danh sách
        }
        // Trả về đối tượng Page chứa danh sách KoiFishAuctionAll
        return new PageImpl<>(responseList, auctionPage.getPageable(), auctionPage.getTotalElements());
    }

    @Transactional
    public void startAuction(Integer auctionId) {
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new IllegalArgumentException("Auction not found"));
        if (auction.getStatus().equals("Scheduled")) {
            auction.setStatus("Ongoing");
            auctionRepository.save(auction);
            System.out.println("Auction " + auctionId + " started.");
        }else{
            rejectAuction(auctionId,-1);
            System.out.println("Auction " + auctionId + " didn't approved.");
        }
    }

    public static void main(String[] args) {
        System.out.println(Instant.now());
    }

    public String deleteAuction(Integer id) {
        if(auctionRepository.findById(id)!=null){
            auctionRepository.delete(id);
            return "Delete successfully";
        }else {
            return "Auction id not found";
        }
    }
    public Long getRevenue(Integer day, Integer month, Integer year) {
        if (day != null && month != null && year != null) {
            return auctionRepository.getRevenueByDay(day, month, year);
        } else if (month != null && year != null) {
            return auctionRepository.getRevenueByMonth(month, year);
        } else if (year != null) {
            return auctionRepository.getRevenueByYear(year);
        }
        throw new IllegalArgumentException("Invalid date parameters. Please provide year, or month and year, or day, month, and year.");
    }

    public Long getCountAuction(Integer day, Integer month, Integer year) {
        if (day != null && month != null && year != null) {
            return auctionRepository.getCountAuctionByDay(day, month, year);
        } else if (month != null && year != null) {
            return auctionRepository.getCountAuctionByMonth(month, year);
        } else if (year != null) {
            return auctionRepository.getCountAuctionByYear(year);
        }
        throw new IllegalArgumentException("Invalid date parameters. Please provide year, or month and year, or day, month, and year.");
    }

    public Long getCountAuctionWithFinishedStatus(Integer day, Integer month, Integer year) {
        if (day != null && month != null && year != null) {
            return auctionRepository.getCountAuctionByDayAndStatus(day, month, year, "Finished");
        } else if (month != null && year != null) {
            return auctionRepository.getCountAuctionByMonthAndStatus(month, year, "Finished");
        } else if (year != null) {
            return auctionRepository.getCountAuctionByYearAndStatus(year, "Finished");
        }
        throw new IllegalArgumentException("Invalid date parameters. Please provide year, or month and year, or day, month, and year.");
    }


}
