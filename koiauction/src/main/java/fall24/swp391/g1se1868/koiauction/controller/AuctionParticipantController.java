package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.StringResponse;
import fall24.swp391.g1se1868.koiauction.model.UserPrinciple;
import fall24.swp391.g1se1868.koiauction.service.AuctionParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auction-participant")
public class AuctionParticipantController {
    @Autowired
    private AuctionParticipantService auctionParticipantService;

    @PostMapping("/participant")
    public ResponseEntity<StringResponse> registerForAuction(@RequestParam Integer auctionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();
        try {
            String response = auctionParticipantService.registerForAuction(userId, auctionId);
            return ResponseEntity.ok(new StringResponse(response));
        }catch(RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.PAYMENT_REQUIRED)
                    .body(new StringResponse(e.getMessage()));
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(new StringResponse(e.getMessage()));
        }
    }
}
