package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.model.auction.AuctionWithMedia;
import fall24.swp391.g1se1868.koiauction.model.auction.KoiAuctionResponseDTO;
import fall24.swp391.g1se1868.koiauction.model.auction.KoiFishAuctionAll;
import fall24.swp391.g1se1868.koiauction.model.auction.KoiInfo;
import fall24.swp391.g1se1868.koiauction.model.koifishdto.KoiFishWithMediaAll;
import fall24.swp391.g1se1868.koiauction.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.Duration;
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

    public Auction getAuctionById(Integer auctionId) {
        return auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found with ID: " + auctionId));
    }


    public List<AuctionWithMedia> getAuctionWithKoiByID(Integer id) {
        Optional<Auction> auctionOptional = auctionRepository.findById(id);
        List<Auction> auctions = new ArrayList<>();

<<<<<<< Updated upstream
// Kiểm tra nếu auction có tồn tại (trong Optional)
        if (auctionOptional.isPresent()) {
            Auction auction = auctionOptional.get();  // Lấy đối tượng Auction
            auctions.add(auction);  // Thêm Auction vào danh sách auctions
        } else {
            throw new RuntimeException("Auction not found with id: " + id);  // Ném ngoại lệ nếu không tìm thấy Auction
=======
    public List<AuctionWithKoi> getOnScheduleAuctionsWithKoi() {
        List<Auction> auctions = auctionRepository.findOnScheduleAuctions();
        return convertToAuctionWithKoi(auctions);
    }

    public List<AuctionWithKoi> getOnGoingAuctionsWithKoi() {
        List<Auction> auctions = auctionRepository.findOngoingAuctions();
        return convertToAuctionWithKoi(auctions);
    }

    private List<AuctionWithKoi> convertToAuctionWithKoi(List<Auction> auctions) {
        List<AuctionWithKoi> auctionWithKoiList = new ArrayList<>();
        for (Auction auction : auctions) {
            List<KoiFish> koiFishList = auctionKoiRepository.findKoiFishByAuctionId(auction.getId());
            auctionWithKoiList.add(new AuctionWithKoi(auction, koiFishList));
>>>>>>> Stashed changes
        }

// Bạn có thể thêm các thông tin khác vào response ở đây

        return convertToAuctionWithKoiAll(auctions);  // Chuyển đổi danh sách auction sang DTO và trả về

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


    public List<AuctionWithMedia> getAuctionsParticipantByUser(int UserID){
        List<Integer> auctionIds = auctionParticipantRepository.findAuctionIdsByUserId(UserID);
        return convertToAuctionWithKoiAll(auctionParticipantRepository.findAuctionsByIds(auctionIds));
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
        auction.setBidderDeposit(request.getStartingPrice()*10/100);
        auction.setBreederDeposit(request.getStartingPrice()*20/100);
        auction.setAuctionFee(500000L);
        auction.setBidStep(request.getBidStep());
        auction.setStatus("Pending");

        Auction savedAuction = auctionRepository.save(auction);

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
    public Auction rejectAuction(Integer auctionId, Integer UserID) {
        Auction auction = auctionRepository.getById(auctionId);
        if (!auction.getStatus().equals("Pending")) {
            throw new IllegalArgumentException("Auction must be in Pending status to be approved.");
        }
        auction.setStatus("Reject");
        auction.setStaffID(UserID);
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
        // Logic for requesting payment from the winner
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
        int winnerID = determineWinner(auction);
        long finalPrice = bidService.getCurrentPrice(auction.getId());
        auction.setFinalPrice(finalPrice);
        auction.setWinnerID(winnerID);
        messagingTemplate.convertAndSend("/topic/auction/" + auction.getId() + "/status", auction);
        processEndOfAuctionTasks(auction);
        auctionRepository.save(auction);
    }
    private List<AuctionWithMedia> convertToAuctionWithKoiAll(List<Auction> auctions) {
        List<AuctionWithMedia> auctionWithMediaList = new ArrayList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        for (Auction auction : auctions) {
            if (authentication == null || !authentication.isAuthenticated() ) {
                auction.setStatus("Please Login or Register to see more");
            } else {
                List<KoiFish> koiFishList = auctionKoiRepository.findKoiFishByAuctionId(auction.getId());
                List<KoiFishWithMediaAll> koiFishWithMediaAllList = new ArrayList<>();
                for (KoiFish koiFish : koiFishList) {
                    List<KoiMedia> koiMediaList = koiMediaRepository.findByKoiID(koiFish);
                    User user = koiFish.getUserID();
                    if (user != null) {
                        user.setPassword("0");
                    }
                    KoiFishWithMediaAll koiFishWithMediaAll = new KoiFishWithMediaAll(koiFish, koiMediaList);
                    koiFishWithMediaAllList.add(koiFishWithMediaAll);
                }
                auctionWithMediaList.add(new AuctionWithMedia(auction, koiFishWithMediaAllList));
            }
        }
        for (Auction auction : auctions) {
            auctionWithMediaList.add(new AuctionWithMedia(auction, new ArrayList<>()));
        }

        return auctionWithMediaList;
    }

//    public List<KoiAuctionResponseDTO> getAuctionDetails(List<Auction> auctions) {
//        List<KoiAuctionResponseDTO> responseList = new ArrayList<>();
//
//        for (Auction auction : auctions) {
//            // Lấy danh sách ID của các KoiFish liên quan đến phiên đấu giá
//            List<Integer> koiFishIds = auctionKoiRepository.findKoiFishByAuctionId(auction.getId())
//                    .stream()
//                    .map(KoiFish::getId)
//                    .collect(Collectors.toList());
//
//            // Tạo đối tượng DTO
//            KoiAuctionResponseDTO response = new KoiAuctionResponseDTO(
//                    auction.getId(),
//                    koiFishIds.isEmpty() ? null : koiFishIds.get(0), // Lấy BreederId từ KoiFish đầu tiên
//                    auction.getStartTime().toString(),
//                    auction.getEndTime().toString(),
//                    auction.getStaffID() != null ? auction.getStaffID() : null,
//                    auction.getWinnerID() != null ? auction.getWinnerID(): null,
//                    auction.getStatus(),
//                    auction.getStartingPrice() ,
//                    auction.getFinalPrice() != null ? auction.getFinalPrice() + " VND" : "N/A",
//                    koiFishIds.isEmpty() ? null : koiFishIds.get(0).getUserID().getFullName(), // BreederName
//                    koiFishIds.isEmpty() ? null : koiFishIds.get(0).getSex(),
//                    koiFishIds.isEmpty() ? null : String.valueOf(koiFishIds.get(0).getBirthday().getYear()),
//                    koiFishIds.isEmpty() ? null : String.valueOf(koiFishIds.get(0).getLength()),
//                    Duration.between(Instant.now(), auction.getEndTime()).toHoursPart() + " Hours " + Duration.between(Instant.now(), auction.getEndTime()).toMinutesPart() + " Minutes",
//                    SecurityContextHolder.getContext().getAuthentication() != null ? "Place a Bid" : "Login / Register to Bid",
//                    koiFishIds // Danh sách ID của KoiFish
//            );
//
//            responseList.add(response);
//        }
//        return responseList;
//    }
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
}
