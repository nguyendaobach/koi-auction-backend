package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.model.auction.AuctionWithMedia;
import fall24.swp391.g1se1868.koiauction.model.auction.KoiAuctionResponseDTO;
import fall24.swp391.g1se1868.koiauction.model.koifishdto.KoiFishWithMediaAll;
import fall24.swp391.g1se1868.koiauction.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

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
    private KoiMediaRepository koiMediaRepository;

    public Auction getAuctionById(Integer auctionId) {
        return auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found with ID: " + auctionId));
    }

    public List<KoiAuctionResponseDTO> getAllAuctionsWithKoi() {
        List<Auction> auctions = auctionRepository.findAll();
        return getAuctionDetails(auctions);
    }

    public List<AuctionWithMedia> getAuctionWithKoiByID(Integer id) {
        Optional<Auction> auctionOptional = auctionRepository.findById(id);
        List<Auction> auctions = new ArrayList<>();

// Kiểm tra nếu auction có tồn tại (trong Optional)
        if (auctionOptional.isPresent()) {
            Auction auction = auctionOptional.get();  // Lấy đối tượng Auction
            auctions.add(auction);  // Thêm Auction vào danh sách auctions
        } else {
            throw new RuntimeException("Auction not found with id: " + id);  // Ném ngoại lệ nếu không tìm thấy Auction
        }

// Bạn có thể thêm các thông tin khác vào response ở đây

        return convertToAuctionWithKoiAll(auctions);  // Chuyển đổi danh sách auction sang DTO và trả về

    }

    public List<KoiAuctionResponseDTO> getOnScheduleAuctionsWithKoi() {
        List<Auction> auctions = auctionRepository.findOnScheduleAuctions();
        return getAuctionDetails(auctions);
    }

    public List<KoiAuctionResponseDTO> getOnGoingAuctionsWithKoi() {
        List<Auction> auctions = auctionRepository.findOngoingAuctions();
        return getAuctionDetails(auctions);
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

    public List<KoiAuctionResponseDTO> getWinnerAuctionByWinnerID(int WinnerID){
        return getAuctionDetails(auctionRepository.getAuctionbyWinnerID(WinnerID));
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
    public List<KoiAuctionResponseDTO> getAllActionRequest(){
        List<Auction> list = auctionRepository.getAllAuctionRequest();
        return getAuctionDetails(list);
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
    public void determineWinner(Auction auction) {
        List<Bid> bids = bidRepository.findByAuctionID(auction.getId());

        if (bids.isEmpty()) {
            System.out.println("No bids found for auction: " + auction.getId());
            return;
        }

        // Assuming the highest bid wins (for ascending auction)
        Bid highestBid = bids.stream().max((b1, b2) -> Long.compare(b1.getAmount(), b2.getAmount())).orElse(null);

        if (highestBid != null) {
            auction.setWinnerID(highestBid.getBidderID().getId()); // Set the auction winner
            System.out.println("Winner determined for auction " + auction.getId() + ": " + highestBid.getBidderID().getFullName());
        }
    }
    // Dummy method for returning deposits to participants
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
        // Example logic to return deposits to all participants who didn't win
        returnDepositsToLosers(auction);

        // Example logic to send payment requests to the winner
        requestPaymentFromWinner(auction);

        System.out.println("End-of-auction tasks completed for auction " + auction.getId());
    }

    public void closeAuction(Auction auction) {
        // Set auction status to Closed
        auction.setStatus("Closed");

        // Determine the winner based on the highest bid
        determineWinner(auction);

        // Notify all users about the auction closure and winner
        messagingTemplate.convertAndSend("/topic/auction/" + auction.getId() + "/status", auction);

        // Additional tasks like returning deposits or requesting payments
        processEndOfAuctionTasks(auction);

        // Save auction state
        auctionRepository.save(auction);
    }
    private List<AuctionWithMedia> convertToAuctionWithKoiAll(List<Auction> auctions) {
        List<AuctionWithMedia> auctionWithMediaList = new ArrayList<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        for (Auction auction : auctions) {
            // Kiểm tra xem người dùng đã đăng nhập chưa
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

        // If the user is not authenticated, return the auctions with updated status
        for (Auction auction : auctions) {
            auctionWithMediaList.add(new AuctionWithMedia(auction, new ArrayList<>()));
        }

        return auctionWithMediaList;
    }

    public List<KoiAuctionResponseDTO> getAuctionDetails(List<Auction>auctions) {
        List<KoiAuctionResponseDTO> responseList = new ArrayList<>();

        for (Auction auction : auctions) {
            // Tìm kiếm tất cả các KoiFish liên quan đến phiên đấu giá này
            List<KoiFish> koiFishList = auctionKoiRepository.findKoiFishByAuctionId(auction.getId());

            for (KoiFish koiFish : koiFishList) {
                // Tìm kiếm ảnh Header Image từ bảng KoiMedia
                KoiMedia headerImage = koiMediaRepository.findByKoiIDAndMediaType(koiFish, "Header Image")
                        .stream()
                        .findFirst()
                        .orElse(null);

                // Tính thời gian còn lại của phiên đấu giá
                Duration duration = Duration.between(Instant.now(), auction.getEndTime());
                String timeLeft = duration.toHoursPart() + " Hours " + duration.toMinutesPart() + " Minutes";

                // Tạo đối tượng DTO với dữ liệu cần thiết
                KoiAuctionResponseDTO response = new KoiAuctionResponseDTO();
                response.setKoiName(koiFish.getKoiName() + " – koi #" + koiFish.getId());
                response.setVariety(koiFish.getKoiTypeID().getTypeName());
                response.setStartingBid(auction.getStartingPrice()+" VND");
                response.setEstimatedValue(auction.getFinalPrice()+" VND"); // Dùng giá cuối nếu có
                response.setBreederName(koiFish.getUserID().getFullName()); // Tên nhà lai tạo
                response.setSex(koiFish.getSex());
                response.setBornIn(String.valueOf(koiFish.getBirthday().getYear()));
                response.setSize(String.valueOf(koiFish.getLength()));
                response.setTimeLeft(timeLeft);
                response.setImageUrl(headerImage != null ? headerImage.getUrl() : "No image available");

                // Kiểm tra xem người dùng đã đăng nhập hay chưa
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
                    // Người dùng đã đăng nhập
                    response.setAction("Place a Bid");
                } else {
                    // Người dùng chưa đăng nhập
                    response.setAction("Login / Register to Bid");
                }

                // Thêm auctionId
                response.setAuctionId(auction.getId());

                // Thêm vào danh sách kết quả
                responseList.add(response);
            }
        }
        return responseList;
    }

}
