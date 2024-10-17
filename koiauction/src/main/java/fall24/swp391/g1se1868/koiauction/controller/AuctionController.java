package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.model.auction.AuctionWithMedia;
import fall24.swp391.g1se1868.koiauction.model.auction.KoiAuctionResponseDTO;
import fall24.swp391.g1se1868.koiauction.model.auction.KoiFishAuctionAll;
import fall24.swp391.g1se1868.koiauction.repository.AuctionRepository;
import fall24.swp391.g1se1868.koiauction.service.AuctionService;
import fall24.swp391.g1se1868.koiauction.service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auction")
public class AuctionController {

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private BidService bidService;


    @GetMapping("/guest/get-all")
    public ResponseEntity<Page<KoiFishAuctionAll>> getAllAuction(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page,size);

        Page<Auction> auctionPage = auctionRepository.findAll(pageable);

        Page<KoiFishAuctionAll> auctionDetails = auctionService.getAllAuction(auctionPage);

        return new ResponseEntity<>(auctionDetails, HttpStatus.OK);
    }

    @GetMapping("/admin/get-all")
    public ResponseEntity<Page<KoiAuctionResponseDTO>> getAuctionDetails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // Tạo Pageable object
        Pageable pageable = PageRequest.of(page, size);

        // Lấy danh sách auction có phân trang từ repository
        Page<Auction> auctionPage = auctionRepository.findAll(pageable);

        // Chuyển đổi đối tượng Page<Auction> thành Page<KoiAuctionResponseDTO>
        Page<KoiAuctionResponseDTO> auctionDetails = auctionService.getAuctionDetails(auctionPage);

        return new ResponseEntity<>(auctionDetails, HttpStatus.OK);
    }

//     Lấy phiên đấu giá theo ID và kèm theo thông tin cá Koi
    @GetMapping("/guest/get-by-id")
    public List<AuctionWithMedia> getAuctionByID(@RequestParam int auctionId) {
        return auctionService.getAuctionWithKoiByID(auctionId);
    }

    // Trả về các phiên đấu giá theo lịch trình kèm theo cá Koi
    @GetMapping("/admin/on-schedule")
    public ResponseEntity<Page<KoiAuctionResponseDTO>> getOnScheduleAuctions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // Tạo Pageable object
        Pageable pageable = PageRequest.of(page, size);

        // Lấy danh sách auction có phân trang từ repository
        Page<Auction> auctionPage = auctionRepository.findOnScheduleAuctions(pageable);

        Page<KoiAuctionResponseDTO> koiAuctionResponseDTOS=auctionService.getAuctionDetails(auctionPage);
        return new ResponseEntity<>(koiAuctionResponseDTOS, HttpStatus.OK);
    }

    // Trả về các phiên đấu giá đang diễn ra kèm theo cá Koi
    @GetMapping("/admin/on-going")
    public  ResponseEntity<Page<KoiAuctionResponseDTO>> getOngoingAuctions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Auction> auctions = auctionRepository.findOngoingAuctions(pageable);
        Page<KoiAuctionResponseDTO> koiAuctionResponseDTOS=auctionService.getAuctionDetails(auctions);
        return new ResponseEntity<>(koiAuctionResponseDTOS, HttpStatus.OK);
    }

    // Trả về các phiên đấu giá trong quá khứ kèm theo người chiến thắng
    @GetMapping("/guest/past")
    public List<Map<String, Object>> getPastAuctionsWithWinnerName() {
        return auctionService.getPastAuctionsWithWinnerName();
    }

    @GetMapping("/user/participant-by-user")
    public List<AuctionWithMedia> getAuctionParticipants() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();
        return auctionService.getAuctionsParticipantByUser(userId);
    }

    @GetMapping("/user/auction-by-winner")
    public ResponseEntity<Page<KoiAuctionResponseDTO>> getWinnerAuctions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();

        // Tạo đối tượng Pageable cho phân trang
        Pageable pageable = PageRequest.of(page, size);

        // Trả về danh sách các phiên đấu giá mà user đã thắng với phân trang
        Page<KoiAuctionResponseDTO> auctionDetails = auctionService.getWinnerAuctionByWinnerID(userId, pageable);
        return new ResponseEntity<>(auctionDetails, HttpStatus.OK);
    }


    @GetMapping("/user/check-participant-for-auction")
    public boolean checkParticipantForAuction(@RequestParam int auctionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();
        return auctionService.isUserParticipantForAuction(userId, auctionId);
    }
    @PostMapping("/breeder/add-auction")
    public ResponseEntity<StringResponse> addAuction(AuctionRequest auctionRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();
        return ResponseEntity.ok(new StringResponse(auctionService.addAuction(auctionRequest, userId)));
    }

    @GetMapping("/staff/get-auction-request")
    public ResponseEntity<Page<KoiAuctionResponseDTO>> getAllAuctionRequest(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<KoiAuctionResponseDTO> auctions = auctionService.getAllActionRequest(pageable);
        return new ResponseEntity<>(auctions, HttpStatus.OK);
    }

    @PostMapping("/staff/approve-auction/{auctionId}")
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
    @PostMapping("/staff/reject-auction/{auctionId}")
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
