package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.repository.AuctionParticipantRepository;
import fall24.swp391.g1se1868.koiauction.repository.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
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

        if (auction.getEndTime().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Auction has already ended.");
        }
        if (!auction.getStatus().equals("Ongoing")) {
            throw new IllegalStateException("Auction is not Ongoing.");
        }
        if (!auctionParticipantRepository.existsByUserIdAndAuctionId(userId, auction.getId())) {
            throw new IllegalArgumentException("User is not registered for the auction.");
        }

        if ("First-come".equalsIgnoreCase(auction.getAuctionMethod()) && hasUserBid(auction.getId(), userId)) {
            throw new IllegalArgumentException("User has already placed a bid in this First-come auction.");
        }

        if("Ascending".equalsIgnoreCase(auction.getAuctionMethod())) {
            Long currentPrice = getCurrentPrice(auction);
            Long stepPrice = auction.getBidStep();
            Long nextPrice = (currentPrice.equals(auction.getStartingPrice())) ? currentPrice : currentPrice + stepPrice;
            if (bid.getAmount() < nextPrice) {
                throw new IllegalArgumentException("Bid must be at least the current highest bid plus the step price.");
            }
            bid.setBidderID(new User(userId));
            bid.getId().setAuctionID(auction.getId());
            ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
            ZonedDateTime now = ZonedDateTime.now(vietnamZone);
            bid.getId().setTime(now.toInstant());
            Bid savedBid  = bidRepository.save(bid);
            notifyBidUpdates(auction.getId(), savedBid);
            if (bid.getAmount().equals(auction.getBuyoutPrice())) {
                auctionService.closeAuction(auction);
            }

            return savedBid;
        }else if("First-come".equalsIgnoreCase(auction.getAuctionMethod())) {
            if (bid.getAmount() < auction.getStartingPrice()) {
                throw new IllegalArgumentException("Bid must be at least the current highest bid plus the step price.");
            }
            bid.setBidderID(new User(userId));
            bid.getId().setAuctionID(auction.getId());
            ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
            ZonedDateTime now = ZonedDateTime.now(vietnamZone);
            bid.getId().setTime(now.toInstant());
            Bid savedBid  = bidRepository.save(bid);
            if (bid.getAmount().equals(auction.getBuyoutPrice())) {
                auctionService.closeAuction(auction);
            }
            return savedBid;
        }
        return null;
    }

    public Long getCurrentPrice(Auction auction) {
        if ("Descending".equalsIgnoreCase(auction.getAuctionMethod())) {
            long startingPrice = auction.getStartingPrice();
            long buyoutPrice = auction.getBuyoutPrice();
            long bidStep = auction.getBidStep();
            long numberOfReductions = (startingPrice - buyoutPrice) / bidStep;
            long reductionInterval = Duration.between(auction.getStartTime(), auction.getEndTime()).toMillis() / numberOfReductions;
            long elapsedIntervals = Duration.between(auction.getStartTime(), Instant.now()).toMillis() / reductionInterval;
            long currentPrice = startingPrice - (elapsedIntervals * bidStep);
            return Math.max(currentPrice, buyoutPrice);
        } else {
            return bidRepository.findHighestBidByAuctionId(auction.getId())
                    .orElse(auction.getStartingPrice());
        }
    }
    public boolean hasUserBid(int auctionId, int userId) {
        return bidRepository.existsByAuctionIdAndBidderId(auctionId, userId);
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
        bidResponse.setUserName(bid.getBidderID().getUserName());
        messagingTemplate.convertAndSend("/topic/auction/" + auctionId + "/bids", bidResponse);
    }


    public List<BidResponseDTO> getBidsWithUserDetails(Integer auctionId) {
        return bidRepository.findBidsWithUserDetails(auctionId);
    }
}

