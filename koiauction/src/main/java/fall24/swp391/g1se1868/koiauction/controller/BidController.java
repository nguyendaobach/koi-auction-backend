package fall24.swp391.g1se1868.koiauction.controller;


import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.service.AuctionService;
import fall24.swp391.g1se1868.koiauction.service.BidService;
import fall24.swp391.g1se1868.koiauction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bid")
public class BidController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private BidService bidService;

    @Autowired
    private AuctionService auctionService;
     @Autowired
     private UserService userService;

    @PostMapping("/place")
    public ResponseEntity<?> placeBid(@RequestBody BidRequest bidRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated.");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();

        Bid bid = new Bid();
        bid.setAuctionID(auctionService.getAuctionById(bidRequest.getAuctionId()));
        bid.setAmount(bidRequest.getAmount());
        try {
            Bid newBid = bidService.placeBid(bid, userId);
            return ResponseEntity.ok(new BidResponseDTO(newBid.getAuctionID().getId()
                    ,newBid.getBidderID().getId(),newBid.getId().getTime(), newBid.getAmount(), userService.getUserById(userId).get().getFullName(), userService.getUserById(userId).get().getUserName()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unknown error.");
        }
    }

    @GetMapping("/get-all")
    public List<BidResponseDTO> getAllBid(@RequestParam int auctionId) {
        return bidService.getBidsWithUserDetails(auctionId);
    }
    @GetMapping("/check-bid")
    public ResponseEntity<?> checkBid(@RequestParam int auctionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated.");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();
        return  ResponseEntity.status(HttpStatus.OK).body(bidService.hasUserBid(auctionId,userId));
    }

}
