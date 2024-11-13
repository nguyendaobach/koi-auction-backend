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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    @Autowired
    private KoiFishRepository koiFishRepository;

    @Autowired
    EmailService emailService;

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
            return null;
        }
    }



    public Page<Map<String, Object>> getPastAuctionsWithWinnerName(Pageable pageable) {
        // Gọi repository để lấy kết quả với phân trang
        Page<Object[]> results = auctionRepository.findPastAuctionsWithWinnerName(pageable);

        // Chuyển đổi kết quả trả về thành một danh sách các Map chứa thông tin cần thiết
        List<Map<String, Object>> pastAuctions = results.getContent().stream()
                .map(result -> {
                    Auction auction = (Auction) result[0];
                    String winnerName = (String) result[1];
                    Map<String, Object> auctionData = new HashMap<>();
                    auctionData.put("auction", auction);
                    auctionData.put("winnerName", winnerName);
                    return auctionData;
                })
                .collect(Collectors.toList());

        // Trả về Page chứa danh sách các đấu giá đã qua với phân trang
        return new PageImpl<>(pastAuctions, pageable, results.getTotalElements());
    }



    public AuctionDetailDTO getAuctionsParticipantByUser(int userId) {
        List<Integer> auctionIds = auctionParticipantRepository.findAuctionIdsByUserId(userId);
        if (!auctionIds.isEmpty()) {
            Integer auctionId = auctionIds.get(0);
            return getAuctionWithKoiByID(auctionId);
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
        Auction auction = getAuctionById(auctionId);
        if(auction.getAuctionMethod().equalsIgnoreCase("Fixed-price")||auction.getAuctionMethod().equalsIgnoreCase("Descending")) return true;
        return auctionParticipantRepository.existsByUserIdAndAuctionId(userId, auctionId);
    }
    @Transactional
    public Auction addAuction(AuctionRequest request, int breederID) {
        Auction auction = new Auction();
        auction.setBreederID(breederID);
        auction.setAuctionMethod(request.getAuctionMethod());
        auction.setStartTime(request.getStartTime());
        auction.setEndTime(request.getEndTime());
        switch (request.getAuctionMethod()) {
            case "Ascending":
                auction.setStartingPrice(request.getStartingPrice());
                auction.setBuyoutPrice(request.getBuyoutPrice());
                auction.setBidStep(request.getBidStep());
                break;

            case "Descending":
                if (request.getStartingPrice() <= request.getBuyoutPrice()) {
                    throw new IllegalArgumentException("Starting price must be greater than buyout price for Descending auction.");
                }
                auction.setStartingPrice(request.getStartingPrice());
                auction.setBuyoutPrice(request.getBuyoutPrice());
                auction.setBidStep(request.getBidStep());
                break;

            case "Fixed-price":
                auction.setStartingPrice(null);
                auction.setBuyoutPrice(request.getBuyoutPrice());
                auction.setBidStep(null);
                break;

            case "First-come":
                auction.setStartingPrice(request.getStartingPrice());
                auction.setBuyoutPrice(request.getBuyoutPrice());
                auction.setBidStep(null);
                break;
            default:
                throw new IllegalArgumentException("Invalid auction method: " + request.getAuctionMethod());
        }

        auction.setBidderDeposit(request.getBidderDeposit());
        if (request.getAuctionMethod().equalsIgnoreCase("Fixed-price")) {
            auction.setBreederDeposit(Math.round(request.getBuyoutPrice() * 0.5 * systemConfigService.getBreederDeposit()));
        } else {
            auction.setBreederDeposit(Math.round(request.getStartingPrice() * systemConfigService.getBreederDeposit()));
        }
        auction.setAuctionFee(systemConfigService.getAuctionFee().longValue());
        auction.setStatus("Pending");
        auction.setCreateAt(Instant.now());
        Auction savedAuction = auctionRepository.save(auction);
        if(request.getAuctionMethod().equalsIgnoreCase("Fixed-price")) {
            walletService.deposit(breederID, Math.round(request.getBuyoutPrice()* 0.5 * systemConfigService.getBreederDeposit())+auction.getAuctionFee(), savedAuction.getId());
        }else {
            walletService.deposit(breederID, Math.round(request.getStartingPrice() * systemConfigService.getBreederDeposit())+auction.getAuctionFee(), savedAuction.getId());
        }
        if (savedAuction != null) {
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
            KoiFish koi = koiFishRepository.findById(koiId)
                    .orElseThrow(() -> new IllegalArgumentException("Koi not found with ID: " + koiId));
            if (!koi.getStatus().equalsIgnoreCase("Active")) {
                throw new RuntimeException("Koi is not active");
            }
            koi.setStatus("Selling");
            koiFishRepository.save(koi);

            AuctionKoi auctionKoi = new AuctionKoi();
            AuctionKoiId auctionKoiId = new AuctionKoiId();
            auctionKoiId.setAuctionID(savedAuction.getId());
            auctionKoiId.setKoiID(koiId);
            auctionKoi.setId(auctionKoiId);
            auctionKoi.setAuctionID(savedAuction);
            auctionKoi.setKoiID(new KoiFish(koiId));
            auctionKoiRepository.save(auctionKoi);
        }
        return savedAuction;
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
    public Auction rejectAuction(Integer auctionId, Integer userId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new EntityNotFoundException("Auction not found"));
        if (!auction.getStatus().equals("Pending")) {
            throw new IllegalArgumentException("Auction must be in Pending status to be rejected.");
        }        auction.setStatus("Reject");
        auction.setStaffID(userId);
        walletService.refund(auction.getBreederID(), auction.getBreederDeposit(), auctionId);
        List<KoiFish> auctionKois = auctionKoiRepository.findKoiFishByAuctionId(auctionId);
        for (KoiFish auctionKoi : auctionKois) {
            auctionKoi.setStatus("Active");
            koiFishRepository.save(auctionKoi);
        }
        return auctionRepository.save(auction);
    }

    @Transactional
    public void updateAuctionStatusOngoing() {
        List<Auction> auctions = auctionRepository.findAll();
        ZonedDateTime nowZoned = ZonedDateTime.now(ZoneId.systemDefault());
        for (Auction auction : auctions) {
            if (auction.getStartTime().isBefore(nowZoned.toInstant()) && (auction.getStatus().equalsIgnoreCase("Pending")|| auction.getStatus().equalsIgnoreCase("Scheduled")) ){
                startAuction(auction.getId());
            }
        }
    }
    public void closeAuctionbyScheduled(){
        List<Auction> auctions = auctionRepository.findAll();
        ZonedDateTime nowZoned = ZonedDateTime.now(ZoneId.systemDefault());
        for (Auction auction : auctions) {
            if (auction.getEndTime().isBefore(nowZoned.toInstant()) && auction.getStatus().equals("Ongoing")) {
                closeAuction(auction);
            }
            }
    }
    public void updateAuctionStatusPaid(Integer auctionId) {
            Auction auction = auctionRepository.getById(auctionId);
            if (auction.getStatus().equals("Closed")) {
                auction.setStatus("Paid");
                auctionRepository.save(auction);
        }
    }


    @Transactional
    public void returnDepositsToLosers(Auction auction) {
        Integer winnerId = null;
         winnerId = auction.getWinnerID();
        if(winnerId != null) {
            List<AuctionParticipant> listap = auctionParticipantRepository.findAuctionParticipantsByAuctionID(auction.getId());
            for(AuctionParticipant auctionParticipant : listap) {
                if(!auctionParticipant.getUserID().getId().equals(winnerId)) {
                    walletService.refund(auctionParticipant.getUserID().getId(),auctionParticipant.getAuctionID().getBidderDeposit(), auction.getId());
                    System.out.println("Refund deposit for Auction " + auction.getStatus() +", User: "+ auctionParticipant.getUserID().getFullName());
                    auctionParticipant.setStatus("Refunded");
                    auctionParticipantRepository.save(auctionParticipant);
                }
            }
        }
    }


    private void requestPaymentFromWinner(Auction auction) {
        System.out.println("Payment request sent to winner for auction: " + auction.getId());
    }

    public void processEndOfAuctionTasks(Auction auction) {
        returnDepositsToLosers(auction);
        requestPaymentFromWinner(auction);
        System.out.println("End-of-auction tasks completed for auction " + auction.getId());
    }
    public void closeAuction(Auction auction) {
        auction.setStatus("Closed");
        if(auction.getAuctionMethod().equalsIgnoreCase("Ascending")) {
            Integer winnerID = determineWinner(auction);
            if (winnerID != null) {
                User winnerUser = userRepository.findById(winnerID).get();
                auction.setWinnerID(winnerID);
                long finalPrice = bidService.getCurrentPrice(auction);
                auction.setFinalPrice(finalPrice);
                List<KoiFish> auctionKois = auctionKoiRepository.findKoiFishByAuctionId(auction.getId());
                for (KoiFish auctionKoi : auctionKois) {
                    auctionKoi.setStatus("Sold");
                    koiFishRepository.save(auctionKoi);
                }
                String winnerSubject = "Chúc mừng bạn đã chiến thắng đấu giá " + auction.getId();
                String winnerText = "Chúc mừng bạn đã chiến thắng đấu giá " + auction.getId() +
                        " với giá " + auction.getFinalPrice() +
                        "VND. Vui lòng thanh toán nếu bạn chưa thanh toán.";
                MailBody mailSendWinner = new MailBody(winnerUser.getEmail(), winnerSubject, winnerText);
                emailService.sendHtmlMessage(mailSendWinner);
                Set<Integer> notifiedLosers = new HashSet<>();
                List<Bid> bids = bidService.getAllBidsForAuction(auction.getId());
                for (Bid bid : bids) {
                    int bidderID = bid.getBidderID().getId();
                    if (bidderID != winnerID && !notifiedLosers.contains(bidderID)) {
                        notifiedLosers.add(bidderID);
                        User loserUser = userRepository.findById(bidderID).get();
                        String loserSubject = "Thông báo về kết quả đấu giá " + auction.getId();
                        String loserText = "Bạn đã không chiến thắng đấu giá " + auction.getId() +
                                ". Tiền cọc của bạn đã được hoàn trả.";
                        MailBody mailSendLoser = new MailBody(loserUser.getEmail(), loserSubject, loserText);
                        emailService.sendHtmlMessage(mailSendLoser);
                    }
                }
            }
            messagingTemplate.convertAndSend("/topic/auction/" + auction.getId(),
                    new AuctionNotification("Closed", winnerID!=null?winnerID:null , auction.getFinalPrice()));
        }else if(auction.getAuctionMethod().equalsIgnoreCase("First-come")) {
            List<Bid> bids = bidRepository.findByAuctionID(auction.getId());
            if (!bids.isEmpty()){
                List<KoiFish> auctionKois = auctionKoiRepository.findKoiFishByAuctionId(auction.getId());
                for (KoiFish auctionKoi : auctionKois) {
                    auctionKoi.setStatus("Sold");
                    koiFishRepository.save(auctionKoi);
                }
            long maxBidAmount = bids.stream().mapToLong(Bid::getAmount).max().orElse(0);
            List<Bid> highestBids = bids.stream()
                    .filter(bid -> bid.getAmount() == maxBidAmount)
                    .collect(Collectors.toList());
            if (highestBids.size() == 1) {
                auction.setFinalPrice(maxBidAmount);
                auction.setWinnerID(highestBids.get(0).getBidderID().getId());
            } else if(highestBids.size() > 1){
                int randomnumber= new Random().nextInt(highestBids.size());
                auction.setFinalPrice(highestBids.get(randomnumber).getAmount());
                auction.setWinnerID(highestBids.get(randomnumber).getBidderID().getId());
            }}
            messagingTemplate.convertAndSend("/topic/auction/" + auction.getId(),
                    new AuctionNotification("Closed", auction.getWinnerID()!=null?auction.getWinnerID():null , auction.getFinalPrice()));
        }
        if(auction.getWinnerID()==null) {
            auction.setStatus("Failed");
            List<KoiFish> auctionKois = auctionKoiRepository.findKoiFishByAuctionId(auction.getId());
            for (KoiFish auctionKoi : auctionKois) {
                auctionKoi.setStatus("Active");
                koiFishRepository.save(auctionKoi);
            }
        }
        processEndOfAuctionTasks(auction);
        auctionRepository.save(auction);
    }
    public void closeAuctionCall(int auctionId, Integer userID) {
        Auction auction = auctionRepository.getById(auctionId);
        auction.setStatus("Closed");
        List<KoiFish> auctionKois = auctionKoiRepository.findKoiFishByAuctionId(auction.getId());
        for (KoiFish auctionKoi : auctionKois) {
            auctionKoi.setStatus("Sold");
            koiFishRepository.save(auctionKoi);
        }
        if (auction.getAuctionMethod().equalsIgnoreCase("Descending")) {
            long finalPrice = calculateDescendingPrice(auction);
            auction.setFinalPrice(finalPrice);
            auction.setWinnerID(userID);
            messagingTemplate.convertAndSend("/topic/auction/" + auction.getId(),
                    new AuctionNotification("Closed", userID, auction.getFinalPrice()));

        } else if (auction.getAuctionMethod().equalsIgnoreCase("Fixed-price")
        || auction.getAuctionMethod().equalsIgnoreCase("Ascending")
        || auction.getAuctionMethod().equalsIgnoreCase("First-come")) {
            auction.setWinnerID(userID);
            auction.setFinalPrice(auction.getBuyoutPrice());
            messagingTemplate.convertAndSend("/topic/auction/" + auction.getId(),
                    new AuctionNotification("Closed", userID, auction.getBuyoutPrice()));
        }
        processEndOfAuctionTasks(auction);
        auctionRepository.save(auction);
    }

    private long calculateDescendingPrice(Auction auction) {
        long priceRange = auction.getStartingPrice() - auction.getBuyoutPrice();
        long stepTimeMillis = (auction.getEndTime().toEpochMilli() - auction.getStartTime().toEpochMilli()) / (priceRange / auction.getBidStep());
        long elapsedSteps = (Instant.now().toEpochMilli() - auction.getStartTime().toEpochMilli()) / stepTimeMillis;
        return auction.getStartingPrice() - (elapsedSteps * auction.getBidStep());
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


    private AuctionDetailDTO convertToAuctionDetailDTO(Auction auction) {
        AuctionDetailDTO auctionDTO = new AuctionDetailDTO();
        auctionDTO.setId(auction.getId());
        auctionDTO.setBreederID(auction.getBreederID());

        // Lấy thông tin Breeder theo BreederID
        User breeder = userRepository.findById(auction.getBreederID())
                .orElseThrow(() -> new RuntimeException("Breeder not found with id: " + auction.getBreederID()));
        auctionDTO.setBreederFullName(breeder.getFullName());
        auctionDTO.setBreederAddress(breeder.getAddress());
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

    public ResponseEntity<?> deleteAuction(Integer id) {
        if(auctionRepository.findById(id).isPresent()){
            auctionRepository.delete(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body( "Delete successfully");
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Auction id not found");
        }
    }
    @Transactional
    public Auction updateAuction(int auctionId, AuctionRequest request, int breederID) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found"));
        if (auction.getBreederID() != breederID) {
            throw new RuntimeException("You do not have permission to update this auction.");
        }

        switch (auction.getStatus()) {
            case "Pending":
                // Save the current breeder deposit for comparison
                long oldBreederDeposit = auction.getBreederDeposit();

                auction.setAuctionMethod(request.getAuctionMethod());
                auction.setStartTime(request.getStartTime());
                auction.setEndTime(request.getEndTime());
                auction.setStartingPrice(request.getStartingPrice());
                auction.setBuyoutPrice(request.getBuyoutPrice());
                auction.setBidderDeposit(request.getBidderDeposit());
                auction.setAuctionFee(systemConfigService.getAuctionFee().longValue());
                auction.setBidStep(request.getBidStep());

                // Calculate the new breeder deposit based on the new starting price
                long newBreederDeposit = Math.round(request.getStartingPrice() * systemConfigService.getBreederDeposit());

                // Adjust deposit only if the new deposit is higher than the old deposit
                if (newBreederDeposit > oldBreederDeposit) {
                    long additionalDeposit = newBreederDeposit - oldBreederDeposit;
                    walletService.deposit(breederID, additionalDeposit, auction.getId());
                    auction.setBreederDeposit(newBreederDeposit);
                } else {
                    // Keep the old deposit if the starting price is lower
                    auction.setBreederDeposit(oldBreederDeposit);
                }

                // Update Koi list for the auction
                auctionKoiRepository.deleteByAuctionID(auction.getId());
                for (Integer koiId : request.getKoiIds()) {
                    AuctionKoi auctionKoi = new AuctionKoi();
                    AuctionKoiId auctionKoiId = new AuctionKoiId();
                    auctionKoiId.setAuctionID(auction.getId());
                    auctionKoiId.setKoiID(koiId);
                    auctionKoi.setId(auctionKoiId);
                    auctionKoi.setAuctionID(auction);
                    auctionKoi.setKoiID(new KoiFish(koiId));
                    auctionKoiRepository.save(auctionKoi);
                }
                break;

            case "Scheduled":
                // Allow only time-related updates for scheduled auctions
                auction.setStartTime(request.getStartTime());
                auction.setEndTime(request.getEndTime());
                break;

            default:
                throw new RuntimeException("Cannot update auction in current status: " + auction.getStatus());
        }

        return auctionRepository.save(auction);
    }


    @Transactional
    public void cancelAuction(Integer id, Integer userId) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found"));
        if(auction.getBreederID()!=userId){
            throw new RuntimeException("You do not have permission to update this auction.");
        }
        switch (auction.getStatus()) {
            case "Pending":
                auction.setStatus("Cancelled");
                walletService.refund(auction.getBreederID(), auction.getBreederDeposit(), id);
                break;

            case "Scheduled":
                List<AuctionParticipant> participants = auctionParticipantRepository.findAuctionParticipantsByAuctionID(id);
                if (participants.isEmpty()) {
                    auction.setStatus("Cancelled");
                    walletService.refund(auction.getBreederID(), auction.getBreederDeposit(), id);
                } else {
                    throw new IllegalArgumentException("Cannot cancel scheduled auction because has participants.");
                }
                break;

            default:
                throw new IllegalArgumentException("Auction cannot be cancelled in current status: " + auction.getStatus());
        }

        auctionRepository.save(auction);
    }

    public Long getRevenue(Integer day, Integer month, Integer year) {
        if (day != null && month != null && year != null) {
            return auctionRepository.getRevenueByDay(day, month, year);
        } else if (month != null && year != null) {
            return auctionRepository.getRevenueByMonth(month, year);
        } else if (year != null) {
            return auctionRepository.getRevenueByYear(year);
        } else {
            return auctionRepository.getTotalRevenue();
        }
    }

    public Long getCountAuction(Integer day, Integer month, Integer year) {
        if (day != null && month != null && year != null) {
            return auctionRepository.getCountAuctionByDay(day, month, year);
        } else if (month != null && year != null) {
            return auctionRepository.getCountAuctionByMonth(month, year);
        } else if (year != null) {
            return auctionRepository.getCountAuctionByYear(year);
        } else {
            return auctionRepository.getTotalAuctionCount();
        }
    }

    public Long getCountAuctionWithFinishedStatus(Integer day, Integer month, Integer year) {
        if (day != null && month != null && year != null) {
            return auctionRepository.getCountAuctionByDayAndStatus(day, month, year, "Finished");
        } else if (month != null && year != null) {
            return auctionRepository.getCountAuctionByMonthAndStatus(month, year, "Finished");
        } else if (year != null) {
            return auctionRepository.getCountAuctionByYearAndStatus(year, "Finished");
        } else {
            return auctionRepository.getTotalFinishedAuctionCount("Finished");
        }
    }

    public Page<Auction> findAuctionsByKoiNameContaining(String koiName, Pageable pageable) {
        return auctionRepository.findAuctionsByKoiNameContaining(koiName, pageable);
    }
}
