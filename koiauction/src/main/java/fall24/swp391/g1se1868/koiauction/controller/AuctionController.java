package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.model.auction.AuctionDetailDTO;
import fall24.swp391.g1se1868.koiauction.model.auction.KoiAuctionResponseDTO;
import fall24.swp391.g1se1868.koiauction.model.auction.KoiFishAuctionAll;
import fall24.swp391.g1se1868.koiauction.repository.AuctionRepository;
import fall24.swp391.g1se1868.koiauction.service.AuctionSchedulerService;
import fall24.swp391.g1se1868.koiauction.service.AuctionService;
import jakarta.persistence.EntityNotFoundException;
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
            @RequestParam(required = false) List<String> status,
            @RequestParam(required = false) List<String> method,
            @RequestParam(defaultValue = "DESC") String desc) {

        Pageable pageable = PageRequest.of(page, size);
         desc=desc.toLowerCase();
        Page<Auction> auctionPage = null;
        if (desc.equals("desc")) {
            auctionPage = auctionRepository.findAllDesc(status, method, pageable);
        } else {
            auctionPage = auctionRepository.findAllAsc(status, method, pageable);
        }
        System.out.println("Request page: " + page + ", size: " + size);
        System.out.println("Total elements: " + auctionPage.getTotalElements());
        System.out.println("Auctions on page " + page + ": " + auctionPage.getContent().size());
        Page<KoiFishAuctionAll> auctionDetails = auctionService.getAllAuction(auctionPage);
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

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchAuctionDetails(
            @RequestParam(required = false, defaultValue = "") String koiName,
            @RequestParam(required = false, defaultValue = "") String bidderName, // Sửa tên tham số ở đây
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> response = auctionService.getAuctionAndKoiDetails(koiName, bidderName, page, size); // Sửa tham số ở đây
        return ResponseEntity.ok(response);
    }



    @GetMapping("/breeder")
    public ResponseEntity<Map<String, Object>> getAllOwnerAuction(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<String> status,
            @RequestParam(required = false) List<String> method,
            @RequestParam(defaultValue = "DESC") String desc) {
        desc =desc.toLowerCase();
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

        System.out.println("Request page: " + page + ", size: " + size);
        System.out.println("Total elements: " + auctionPage.getTotalElements());
        System.out.println("Auctions on page " + page + ": " + auctionPage.getContent().size());

        Page<KoiFishAuctionAll> auctionDetails = auctionService.getAllAuction(auctionPage);

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
    public ResponseEntity<AuctionDetailDTO> getAuctionByID(@PathVariable int id) {
        AuctionDetailDTO auctionDetailDTO = auctionService.getAuctionWithKoiByID(id);
        if (auctionDetailDTO != null) {
            return ResponseEntity.ok(auctionDetailDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }



    @GetMapping("/past")
    public ResponseEntity<Map<String, Object>> getPastAuctionsWithWinnerName(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // Tạo đối tượng Pageable cho phân trang
        Pageable pageable = PageRequest.of(page, size);

        // Lấy danh sách các phiên đấu giá đã qua với phân trang
        Page<Map<String, Object>> pastAuctions = auctionService.getPastAuctionsWithWinnerName(pageable);

        // Tạo đối tượng Map để trả về thông tin phân trang và danh sách đấu giá
        Map<String, Object> response = new HashMap<>();
        response.put("pastAuctions", pastAuctions.getContent());  // Danh sách các đấu giá đã qua
        response.put("currentPage", pastAuctions.getNumber());  // Trang hiện tại
        response.put("totalPages", pastAuctions.getTotalPages());  // Tổng số trang
        response.put("totalElements", pastAuctions.getTotalElements());  // Tổng số phần tử

        // Trả về kết quả với mã trạng thái OK
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/user/participant")
    public ResponseEntity<?> getAuctionParticipants() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();
        try {
            return  ResponseEntity.ok(auctionService.getAuctionsParticipantByUser(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @GetMapping("/user/auction-by-winner")
    public ResponseEntity<Map<String, Object>> getWinnerAuctions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Kiểm tra người dùng có xác thực không
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }

        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();

        // Tạo đối tượng Pageable cho phân trang
        Pageable pageable = PageRequest.of(page, size);

        // Lấy danh sách các phiên đấu giá mà người dùng đã thắng
        Page<KoiAuctionResponseDTO> auctionDetails = auctionService.getWinnerAuctionByWinnerID(userId, pageable);

        // Tạo đối tượng Map để trả về thông tin phân trang và danh sách đấu giá
        Map<String, Object> response = new HashMap<>();
        response.put("auctions", auctionDetails.getContent());  // Danh sách các phiên đấu giá
        response.put("currentPage", auctionDetails.getNumber());  // Trang hiện tại
        response.put("totalPages", auctionDetails.getTotalPages());  // Tổng số trang
        response.put("totalElements", auctionDetails.getTotalElements());  // Tổng số phần tử

        return new ResponseEntity<>(response, HttpStatus.OK);  // Trả về kết quả với mã trạng thái OK
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
    @PostMapping("/user/close-auction")
    public ResponseEntity<?> closeAuction(@RequestParam int auctionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();
        try{
            auctionService.closeAuctionCall(auctionId,userId);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/breeder/add-auction")
    public ResponseEntity<AuctionResponse> addAuction(@RequestBody AuctionRequest auctionRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();
        if(auctionRequest.getAuctionMethod().equalsIgnoreCase("Ascending")) {
            if (auctionRequest.getBidStep() == null || auctionRequest.getStartingPrice() == null || auctionRequest.getBuyoutPrice() == null || auctionRequest.getBidderDeposit() == null ||
                    auctionRequest.getBidStep() < 100000 || auctionRequest.getStartingPrice() < 100000 || auctionRequest.getBuyoutPrice() < 10000 || auctionRequest.getBidderDeposit() < 100000) {
                return ResponseEntity.badRequest().body(new AuctionResponse("Price values must be greater than 100000 and not null", null));
            }
        }
        if(auctionRequest.getAuctionMethod().equalsIgnoreCase("Descending")) {
            if (auctionRequest.getBidStep() == null || auctionRequest.getStartingPrice() == null || auctionRequest.getBuyoutPrice() == null ||
                    auctionRequest.getBidStep() < 100000 || auctionRequest.getStartingPrice() < 100000 || auctionRequest.getBuyoutPrice() < 10000 ) {
                return ResponseEntity.badRequest().body(new AuctionResponse("Price values must be greater than 100000 and not null", null));
            }
        }
        if(auctionRequest.getAuctionMethod().equalsIgnoreCase("Fixed-price")) {
            if ( auctionRequest.getBuyoutPrice() == null
                     || auctionRequest.getBuyoutPrice() < 10000 ) {
                return ResponseEntity.badRequest().body(new AuctionResponse("Price values must be greater than 100000 and not null", null));
            }
        }
        if(auctionRequest.getAuctionMethod().equalsIgnoreCase("Fist-come")) {
            if ( auctionRequest.getStartingPrice() == null || auctionRequest.getBuyoutPrice() == null || auctionRequest.getBidderDeposit() == null ||
                    auctionRequest.getStartingPrice() < 100000 || auctionRequest.getBuyoutPrice() < 10000 || auctionRequest.getBidderDeposit() < 100000) {
                return ResponseEntity.badRequest().body(new AuctionResponse("Price values must be greater than 100000 and not null", null));
            }
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
    public ResponseEntity<Map<String, Object>> getAllAuctionRequest(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // Tạo đối tượng Pageable cho phân trang
        Pageable pageable = PageRequest.of(page, size);

        Page<KoiAuctionResponseDTO> auctionRequests = auctionService.getAllActionRequest(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("auctionRequests", auctionRequests.getContent());
        response.put("currentPage", auctionRequests.getNumber());
        response.put("totalPages", auctionRequests.getTotalPages());
        response.put("totalElements", auctionRequests.getTotalElements());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/staff/auction/{auctionId}")
    public ResponseEntity<Auction> handleAuctionAction(
            @PathVariable Integer auctionId,
            @RequestParam String action) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }

        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        int userId = userPrinciple.getId();

        Auction updatedAuction;
        if ("approve".equalsIgnoreCase(action)) {
            updatedAuction = auctionService.approveAuction(auctionId, userId);
        } else if ("reject".equalsIgnoreCase(action)) {
            updatedAuction = auctionService.rejectAuction(auctionId, userId);
        } else {
            throw new RuntimeException("Invalid action. Use 'approve' or 'reject'.");
        }

        return ResponseEntity.ok(updatedAuction);
    }
    @GetMapping("/{auctionId}/winner")
    public ResponseEntity<?> getUserWin(@PathVariable Integer auctionId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new StringResponse("User is not authenticated"));
            }
            UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
            int userId = userPrinciple.getId();
            Map<String, Object> winner = auctionService.getWinnerByAuction(auctionId, userId);
            return ResponseEntity.ok(winner);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new StringResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new StringResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new StringResponse("An unexpected error occurred: " + e.getMessage()));
        }

    }
}
