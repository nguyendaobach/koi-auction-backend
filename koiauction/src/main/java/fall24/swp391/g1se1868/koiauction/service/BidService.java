package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.repository.AuctionParticipantRepository;
import fall24.swp391.g1se1868.koiauction.repository.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private final Map<Integer, List<SseEmitter>> subscribers = new ConcurrentHashMap<>();

    public Bid placeBid(Bid bid, int userId) {
        if (bid.getId() == null) {
            bid.setId(new BidId());
        }
        Auction auction = bid.getAuctionID();
        if (!auction.getStatus().equals("Ongoing")) {
            throw new IllegalStateException("Auction is not Ongoing.");
        }
        if (!auctionParticipantRepository.existsByUserIdAndAuctionId(userId, auction.getId())) {
            throw new IllegalArgumentException("User is not registered for the auction.");
        }
        Long currentPrice = getCurrentPrice(auction.getId());
        Long stepPrice = auction.getBidStep();
        Long nextPrice;
        if(currentPrice == auction.getStartingPrice()){
            nextPrice = currentPrice;
        }else{
            nextPrice = currentPrice + stepPrice;
        }
        if (bid.getAmount() < nextPrice) {
            throw new IllegalArgumentException("Bid must be at least the current highest bid plus the step price.");
        }
        bid.setBidderID(new User(userId));
        bid.getId().setAuctionID(auction.getId());
        ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime now = ZonedDateTime.now(vietnamZone);
        bid.getId().setTime(now.toInstant());
        Bid savedBid = bidRepository.save(bid);
        notifyBidUpdates(auction.getId(), savedBid);
        if (bid.getAmount().equals(auction.getBuyoutPrice())) {
            auctionService.closeAuction(auction);
        }
        return savedBid;
    }
    public Long getCurrentPrice(int auctionID){
        return bidRepository.findHighestBidByAuctionId(auctionID).orElse(auctionService.getAuctionById(auctionID).getStartingPrice());
    }


    public List<Bid> getAllBidsForAuction(Integer auctionId) {
        return bidRepository.findByAuctionID(auctionId);
    }

    public void notifyBidUpdates(Integer auctionId, Bid bid) {
        // Tạo đối tượng BidResponseDTO
        BidResponseDTO bidResponse = new BidResponseDTO();
        bidResponse.setAuctionId(auctionId);
        bidResponse.setBidderId(bid.getBidderID().getId());
        bidResponse.setBidTime(bid.getId().getTime());
        bidResponse.setAmount(bid.getAmount());
        bidResponse.setFullName(bid.getBidderID().getFullName());
        messagingTemplate.convertAndSend("/topic/auction/" + auctionId + "/bids", bidResponse);
    }


    public List<BidResponseDTO> getBidsWithUserDetails(Integer auctionId) {
        return bidRepository.findBidsWithUserDetails(auctionId);
    }
}

