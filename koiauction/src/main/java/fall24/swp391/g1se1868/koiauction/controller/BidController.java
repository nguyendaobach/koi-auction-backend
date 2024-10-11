package fall24.swp391.g1se1868.koiauction.controller;


import fall24.swp391.g1se1868.koiauction.model.Bid;
import fall24.swp391.g1se1868.koiauction.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bid")
public class BidController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private AuctionService auctionService;

    @PostMapping("/place")
    public Bid placeBid(@RequestBody Bid bid) {
        Bid newBid = auctionService.placeBid(bid); // Place bid logic here

        // Broadcast the updated bid list to all connected users
        messagingTemplate.convertAndSend("/topic/auction/" + bid.getAuction().getId(), auctionService.getAllBidsForAuction(bid.getAuction().getId()));
        return newBid;
    }
}
