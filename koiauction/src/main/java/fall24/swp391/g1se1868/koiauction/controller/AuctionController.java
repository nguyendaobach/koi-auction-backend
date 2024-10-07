package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.Auction;
import fall24.swp391.g1se1868.koiauction.model.UserPrinciple;
import fall24.swp391.g1se1868.koiauction.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auction")
public class AuctionController {
    @Autowired
    private AuctionService auctionService;

    @GetMapping("/get-all")
    public List<Auction> getAllAuctions() {
        return auctionService.getAllAuctions();
    }
    @GetMapping("/get-by-id")
    public Auction getOnScheduleAuctions(@RequestParam int auctionId) {
        return auctionService.getAuctionByID(auctionId);
    }

    @GetMapping("/on-schedule")
    public List<Auction> getOnScheduleAuctions() {
        return auctionService.getOnScheduleAuctions();
    }


    @GetMapping("/on-going")
    public List<Auction> getOngoingAuctions() {
        return auctionService.getOnGoingAuctions();
    }

    @GetMapping("/past")
    public List<Map<String, Object>> getPastAuctionsWithWinnerName() {
        return auctionService.getPastAuctionsWithWinnerName();
    }
    @GetMapping("/participant-by-user")
    public List<Auction> getAuctionParticipants() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();
        return auctionService.getAuctionsParticipantByUser(userId);
    }
    @GetMapping("/auction-by-winner")
    public List<Auction> getWinnerAuctions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();
        return auctionService.getWinnerAuctionByWinnerID(userId);
    }
    @GetMapping("/check-participant-for-auction")
    public boolean checkParticipantForAuction(@RequestParam int auctionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();
        return auctionService.isUserParticipantForAuction(userId,auctionId);
    }

}
