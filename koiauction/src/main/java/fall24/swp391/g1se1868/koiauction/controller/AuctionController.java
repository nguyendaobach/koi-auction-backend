package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    // Trả về tất cả các phiên đấu giá kèm theo thông tin cá Koi
    @GetMapping("/get-all")
    public List<AuctionWithKoi> getAllAuctions() {
        return auctionService.getAllAuctionsWithKoi();
    }

    // Lấy phiên đấu giá theo ID và kèm theo thông tin cá Koi
    @GetMapping("/get-by-id")
    public AuctionWithKoi getAuctionByID(@RequestParam int auctionId) {
        return auctionService.getAuctionWithKoiByID(auctionId);
    }

    // Trả về các phiên đấu giá theo lịch trình kèm theo cá Koi
    @GetMapping("/on-schedule")
    public List<AuctionWithKoi> getOnScheduleAuctions() {
        return auctionService.getOnScheduleAuctionsWithKoi();
    }

    // Trả về các phiên đấu giá đang diễn ra kèm theo cá Koi
    @GetMapping("/on-going")
    public List<AuctionWithKoi> getOngoingAuctions() {
        return auctionService.getOnGoingAuctionsWithKoi();
    }

    // Trả về các phiên đấu giá trong quá khứ kèm theo người chiến thắng
    @GetMapping("/past")
    public List<Map<String, Object>> getPastAuctionsWithWinnerName() {
        return auctionService.getPastAuctionsWithWinnerName();
    }

    @GetMapping("/participant-by-user")
    public List<AuctionWithKoi> getAuctionParticipants() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();
        return auctionService.getAuctionsParticipantByUser(userId);
    }

    // Trả về các phiên đấu giá mà người dùng đã thắng
    @GetMapping("/auction-by-winner")
    public List<AuctionWithKoi> getWinnerAuctions() {
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
        return auctionService.isUserParticipantForAuction(userId, auctionId);
    }
    @PostMapping("/add-auction")
    public ResponseEntity<StringResponse> addAuction(AuctionRequest auctionRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();
        return ResponseEntity.ok(new StringResponse(auctionService.addAuction(auctionRequest, userId)));
    }
}
