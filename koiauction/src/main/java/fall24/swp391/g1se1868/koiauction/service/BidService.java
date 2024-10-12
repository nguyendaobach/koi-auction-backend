package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.Auction;
import fall24.swp391.g1se1868.koiauction.model.Bid;
import fall24.swp391.g1se1868.koiauction.model.BidId;
import fall24.swp391.g1se1868.koiauction.model.User;
import fall24.swp391.g1se1868.koiauction.repository.AuctionParticipantRepository;
import fall24.swp391.g1se1868.koiauction.repository.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class BidService {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private AuctionParticipantRepository auctionParticipantRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Bid placeBid(Bid bid, int userId) {
        // Ensure that the BidId is initialized
        if (bid.getId() == null) {
            bid.setId(new BidId());
        }

        // Get the auction from the bid object
        Auction auction = bid.getAuctionID();  // Use getAuction() instead of getAuctionID()

        // Check if the auction is ongoing
        if (!auction.getStatus().equals("Ongoing")) {
            throw new IllegalStateException("Auction is not Ongoing.");
        }

        // Check if the user is registered for the auction
        if (!auctionParticipantRepository.existsByUserIdAndAuctionId(userId, auction.getId())) {
            throw new IllegalArgumentException("User is not registered for the auction.");
        }

        // Set the bidder ID
        bid.setBidderID(new User(userId));  // Assuming you have a User constructor that accepts userId

        // Set the current time for the bid
        ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime now = ZonedDateTime.now(vietnamZone);
        bid.getId().setTime(now.toInstant());  // Ensure the BidId is initialized

        // Set the auction ID in the BidId if needed
        bid.getId().setAuctionID(auction.getId());  // Set auction ID in BidId

        // Save the bid and notify updates
        Bid savedBid = bidRepository.save(bid);
        notifyBidUpdates(auction.getId());

        return savedBid;
    }

    // Method to get all bids for an auction
    public List<Bid> getAllBidsForAuction(Integer auctionId) {
        return bidRepository.findByAuctionID(auctionId);
    }

    // Notify users about auction bid updates
    public void notifyBidUpdates(Integer auctionId) {
        List<Bid> updatedBids = getAllBidsForAuction(auctionId);
        messagingTemplate.convertAndSend("/topic/auction/" + auctionId, updatedBids);
    }
}
