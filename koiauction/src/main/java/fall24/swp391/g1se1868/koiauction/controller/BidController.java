package fall24.swp391.g1se1868.koiauction.controller;


import fall24.swp391.g1se1868.koiauction.model.Bid;
import fall24.swp391.g1se1868.koiauction.model.BidRequest;
import fall24.swp391.g1se1868.koiauction.model.UserPrinciple;
import fall24.swp391.g1se1868.koiauction.service.AuctionService;
import fall24.swp391.g1se1868.koiauction.service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/bid")
public class BidController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private BidService bidService;

    @Autowired
    private AuctionService auctionService;

    @PostMapping("/place")
    public Bid placeBid(@RequestBody BidRequest bidRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();

        // Create a new Bid object with the BidRequest data
        Bid bid = new Bid();
        bid.setAuctionID(auctionService.getAuctionById(bidRequest.getAuctionId()));
        bid.setAmount(bidRequest.getAmount());

        Bid newBid = bidService.placeBid(bid, userId);

        // Broadcast the updated bid list to all connected users
        messagingTemplate.convertAndSend("/topic/auction/" + bid.getAuctionID().getId(), bidService.getAllBidsForAuction(bid.getAuctionID().getId()));
        return newBid;
    }

}
