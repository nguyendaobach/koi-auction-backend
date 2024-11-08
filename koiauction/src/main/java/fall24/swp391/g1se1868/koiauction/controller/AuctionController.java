package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.model.auction.AuctionDetailDTO;
import fall24.swp391.g1se1868.koiauction.model.auction.KoiAuctionResponseDTO;
import fall24.swp391.g1se1868.koiauction.model.auction.KoiFishAuctionAll;
import fall24.swp391.g1se1868.koiauction.repository.AuctionRepository;
import fall24.swp391.g1se1868.koiauction.service.AuctionSchedulerService;
import fall24.swp391.g1se1868.koiauction.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
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
    private AuctionSchedulerService auctionSchedulerService;

    @GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> getAllAuction(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<String> status,  // Danh sách trạng thái
            @RequestParam(required = false) List<String> method,  // Danh sách phương thức
            @RequestParam(defaultValue = "DESC") String desc1) { // Mô tả

        Pageable pageable = PageRequest.of(page, size);
        String desc=desc1.toLowerCase();
        Page<Auction> auctionPage = null;
        if (desc.equals("desc")) {
            auctionPage = auctionRepository.findAllDesc(status, method, pageable);
        } else {
            auctionPage = auctionRepository.findAllAsc(status, method, pageable);
        }

        // Log thông tin về phiên đấu giá
        System.out.println("Request page: " + page + ", size: " + size);
        System.out.println("Total elements: " + auctionPage.getTotalElements());
        System.out.println("Auctions on page " + page + ": " + auctionPage.getContent().size());

        // Gọi service để lấy thông tin chi tiết
        Page<KoiFishAuctionAll> auctionDetails = auctionService.getAllAuction(auctionPage);

        // Tạo một Map để chứa thông tin phản hồi
        Map<String, Object> response = new HashMap<>();
        response.put("auctions", auctionDetails.getContent());
        response.put("currentPage", auctionDetails.getNumber()); // Trang hiện tại
        response.put("totalPages", auctionDetails.getTotalPages()); // Tổng số trang
        response.put("totalElements", auctionDetails.getTotalElements()); // Tổng số phần tử

        if (auctionDetails != null && !auctionDetails.isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Trả về 404 nếu không tìm thấy phiên đấu giá
        }
    }

    @GetMapping("/breeder")
    public ResponseEntity<Map<String, Object>> getAllOwnerAuction(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<String> status,  // Danh sách trạng thái
            @RequestParam(required = false) List<String> method,  // Danh sách phương thức
            @RequestParam(defaultValue = "DESC") String desc1) { // Mô tả
        String desc =desc1.toLowerCase();
        Pageable pageable = PageRequest.of(page, size);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();

        Page<Auction> auctionPage = null;
        if (desc.equals("desc")) {
            auctionPage = auctionRepository.findAllOwnerDesc(status, method,userId, pageable);
        } else {
            auctionPage = auctionRepository.findAllOwnerAsc(status, method,userId, pageable);
        }

        // Log thông tin về phiên đấu giá
        System.out.println("Request page: " + page + ", size: " + size);
        System.out.println("Total elements: " + auctionPage.getTotalElements());
        System.out.println("Auctions on page " + page + ": " + auctionPage.getContent().size());

        // Gọi service để lấy thông tin chi tiết
        Page<KoiFishAuctionAll> auctionDetails = auctionService.getAllAuction(auctionPage);

        // Tạo một Map để chứa thông tin phản hồi
        Map<String, Object> response = new HashMap<>();
        response.put("auctions", auctionDetails.getContent());
        response.put("currentPage", auctionDetails.getNumber()); // Trang hiện tại
        response.put("totalPages", auctionDetails.getTotalPages()); // Tổng số trang
        response.put("totalElements", auctionDetails.getTotalElements()); // Tổng số phần tử

        if (auctionDetails != null && !auctionDetails.isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Trả về 404 nếu không tìm thấy phiên đấu giá
        }
    }


    @GetMapping("/admin")
    public ResponseEntity<Map<String, Object>> getAuctionDetails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Auction> auctionPage = auctionRepository.findAllAdmin(pageable);
        Page<KoiAuctionResponseDTO> koiAuctionResponseDTOList =auctionService.getAuctionDetails(auctionPage);
        Map<String, Object> response = new HashMap<>();
        response.put("auctions", koiAuctionResponseDTOList.getContent());
        response.put("currentPage", koiAuctionResponseDTOList.getNumber()); // Trang hiện tại
        response.put("totalPages", koiAuctionResponseDTOList.getTotalPages()); // Tổng số trang
        response.put("totalElements", koiAuctionResponseDTOList.getTotalElements()); // Tổng số phần tử
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public AuctionDetailDTO getAuctionByID(@PathVariable int id) {
        return auctionService.getAuctionWithKoiByID(id);
    }



    // Trả về các phiên đấu giá trong quá khứ kèm theo người chiến thắng
    @GetMapping("/past")
    public List<Map<String, Object>> getPastAuctionsWithWinnerName() {
        return auctionService.getPastAuctionsWithWinnerName();
    }

    @GetMapping("/user/participant-by-user")
    public AuctionDetailDTO getAuctionParticipants() {
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
    public ResponseEntity<AuctionResponse> addAuction(@RequestBody AuctionRequest auctionRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();
        if (auctionRequest.getBidStep() == null || auctionRequest.getStartingPrice() == null || auctionRequest.getBuyoutPrice() == null || auctionRequest.getBidderDeposit() == null ||
                auctionRequest.getBidStep() < 100000 || auctionRequest.getStartingPrice() < 100000 || auctionRequest.getBuyoutPrice() < 10000 || auctionRequest.getBidderDeposit() <100000) {
            return ResponseEntity.badRequest().body(new AuctionResponse("Price values must be greater than 100000 and not null",null));
        }
        if (auctionRequest.getStartTime() == null || auctionRequest.getEndTime() == null ||
                auctionRequest.getStartTime().isBefore(Instant.now()) || auctionRequest.getEndTime().isBefore(Instant.now())) {
            return ResponseEntity.badRequest().body(new AuctionResponse("Time invalid",null));
        }
        try {
            Auction auctionResult = auctionService.addAuction(auctionRequest, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(new AuctionResponse("Add Auction Successfully",auctionResult.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuctionResponse(e.getMessage(),null));
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAuction(
            @PathVariable int id,
            @RequestBody AuctionRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();
        try {
            Auction updatedAuction = auctionService.updateAuction(id, request, userId);
            return ResponseEntity.ok(updatedAuction);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelAuction(@PathVariable Integer id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();
        try {
            auctionService.cancelAuction(id,userId);
            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("message", "Auction cancelled successfully.");
            return ResponseEntity.ok(successResponse);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
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

    @PostMapping("/staff/approve/{auctionId}")
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
    @PostMapping("/staff/reject/{auctionId}")
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
