package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.model.auction.AuctionWithMedia;
import fall24.swp391.g1se1868.koiauction.model.auction.KoiAuctionResponseDTO;
import fall24.swp391.g1se1868.koiauction.repository.AuctionRepository;
import fall24.swp391.g1se1868.koiauction.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Autowired
    private AuctionRepository auctionRepository;


    @GetMapping("/get-all")
    public ResponseEntity<List<KoiAuctionResponseDTO>> getAuctionDetails() {
        List<Auction> auctions=auctionRepository.findAll();
        List<KoiAuctionResponseDTO> auctionDetails = auctionService.getAuctionDetails(auctions);
        return new ResponseEntity<>(auctionDetails, HttpStatus.OK);
    }

//     Lấy phiên đấu giá theo ID và kèm theo thông tin cá Koi
    @GetMapping("/get-by-id")
    public List<AuctionWithMedia> getAuctionByID(@RequestParam int auctionId) {
        return auctionService.getAuctionWithKoiByID(auctionId);
    }

    // Trả về các phiên đấu giá theo lịch trình kèm theo cá Koi
    @GetMapping("/on-schedule")
    public ResponseEntity<List<KoiAuctionResponseDTO>> getOnScheduleAuctions() {
        List<Auction> auctions=auctionRepository.findOnScheduleAuctions();
        List<KoiAuctionResponseDTO> koiAuctionResponseDTOS=auctionService.getAuctionDetails(auctions);
        return new ResponseEntity<>(koiAuctionResponseDTOS, HttpStatus.OK);
    }

    // Trả về các phiên đấu giá đang diễn ra kèm theo cá Koi
    @GetMapping("/on-going")
    public  ResponseEntity<List<KoiAuctionResponseDTO>> getOngoingAuctions() {
        List<Auction> auctions = auctionRepository.findOngoingAuctions();
        List<KoiAuctionResponseDTO> koiAuctionResponseDTOS=auctionService.getAuctionDetails(auctions);
        return new ResponseEntity<>(koiAuctionResponseDTOS, HttpStatus.OK);
    }

    // Trả về các phiên đấu giá trong quá khứ kèm theo người chiến thắng
    @GetMapping("/past")
    public List<Map<String, Object>> getPastAuctionsWithWinnerName() {
        return auctionService.getPastAuctionsWithWinnerName();
    }

    @GetMapping("/participant-by-user")
    public List<AuctionWithMedia> getAuctionParticipants() {
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
    public List<KoiAuctionResponseDTO> getWinnerAuctions() {
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

    @GetMapping("/get-auction-requets")
        public List<KoiAuctionResponseDTO> getAuctionRequets(){
            return auctionService.getAllActionRequest();
        }

    @PostMapping("/approve-auction/{auctionId}")
    public ResponseEntity<Auction> approveAuction(@PathVariable Integer auctionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();
        Auction approvedAuction = auctionService.approveAuction(auctionId, userId);
        return ResponseEntity.ok(approvedAuction);
    }
    @PostMapping("/reject-auction/{auctionId}")
    public ResponseEntity<Auction> rejectAuction(@PathVariable Integer auctionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();
        Auction approvedAuction = auctionService.rejectAuction(auctionId, userId);
        return ResponseEntity.ok(approvedAuction);
    }

}
