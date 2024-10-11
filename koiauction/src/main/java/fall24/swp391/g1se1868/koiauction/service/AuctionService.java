package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.repository.AuctionKoiRepository;
import fall24.swp391.g1se1868.koiauction.repository.AuctionParticipantRepository;
import fall24.swp391.g1se1868.koiauction.repository.AuctionRepository;
import fall24.swp391.g1se1868.koiauction.repository.BidRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Auction getAuctionById(Integer auctionId) {
        return auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found with ID: " + auctionId));
    }

    public List<AuctionWithKoi> getAllAuctionsWithKoi() {
        List<Auction> auctions = auctionRepository.findAll();
        return convertToAuctionWithKoi(auctions);
    }

    public AuctionWithKoi getAuctionWithKoiByID(Integer id) {
        Auction auction = auctionRepository.getById(id);
        List<KoiFish> koiFishList = auctionKoiRepository.findKoiFishByAuctionId(id);
        return new AuctionWithKoi(auction, koiFishList);
    }

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
        }
        return auctionWithKoiList;
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


    public List<AuctionWithKoi> getAuctionsParticipantByUser(int UserID){
        List<Integer> auctionIds = auctionParticipantRepository.findAuctionIdsByUserId(UserID);
        return convertToAuctionWithKoi(auctionParticipantRepository.findAuctionsByIds(auctionIds));
    }

    public List<AuctionWithKoi> getWinnerAuctionByWinnerID(int WinnerID){
        return convertToAuctionWithKoi(auctionRepository.getAuctionbyWinnerID(WinnerID));
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
    public List<AuctionWithKoi> getAllActionRequest(){
        List<Auction> list = auctionRepository.getAllAuctionRequest();
        return convertToAuctionWithKoi(list);
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

}
