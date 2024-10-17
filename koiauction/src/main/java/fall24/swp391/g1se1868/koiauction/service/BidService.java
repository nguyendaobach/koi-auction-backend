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
        notifyBidUpdates(auction.getId());
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

    public void notifyBidUpdates(Integer auctionId) {
        List<Bid> updatedBids = getAllBidsForAuction(auctionId);
        messagingTemplate.convertAndSend("/topic/auction/" + auctionId, updatedBids);
    }
    //-------------------------------------------------------------------

    public Bid placeBidV2(Bid bid, int userId) {
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
        Long nextPrice = currentPrice.equals(auction.getStartingPrice()) ? currentPrice : currentPrice + stepPrice;

        if (bid.getAmount() < nextPrice) {
            throw new IllegalArgumentException("Bid must be at least the current highest bid plus the step price.");
        }

        bid.setBidderID(new User(userId));
        bid.getId().setAuctionID(auction.getId());
        ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime now = ZonedDateTime.now(vietnamZone);
        bid.getId().setTime(now.toInstant());

        Bid savedBid = bidRepository.save(bid);
        notifyBidUpdatesV2(auction.getId());

        if (bid.getAmount().equals(auction.getBuyoutPrice())) {
            auctionService.closeAuctionv2(auction);
        }

        return savedBid;
    }

    public void addSubscriber(Integer auctionId, SseEmitter emitter) {
        List<SseEmitter> emitters = subscribers.computeIfAbsent(auctionId, k -> new ArrayList<>());

        // Giới hạn số lượng subscribers, ví dụ giới hạn 100
        if (emitters.size() > 100) {
            emitters.remove(0); // Loại bỏ emitter cũ nhất
        }

        emitters.add(emitter);
    }

    public void removeSubscriber(Integer auctionId, SseEmitter emitter) {
        List<SseEmitter> emitters = subscribers.get(auctionId);
        if (emitters != null) {
            emitters.remove(emitter);
        }
    }

    public void notifyBidUpdatesV2(Integer auctionId) {
        List<SseEmitter> emitters = subscribers.get(auctionId);
        if (emitters != null && !emitters.isEmpty()) {
            List<Bid> bids = getAllBidsForAuction(auctionId);

            Iterator<SseEmitter> iterator = emitters.iterator();
            while (iterator.hasNext()) {
                SseEmitter emitter = iterator.next();
                try {
                    emitter.send(SseEmitter.event().name("bidUpdate").data(bids));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                    iterator.remove();  // Loại bỏ emitter gặp lỗi
                }
            }
        }
    }
}

