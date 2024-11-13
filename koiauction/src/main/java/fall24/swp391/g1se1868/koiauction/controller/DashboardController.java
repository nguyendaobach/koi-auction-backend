package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping()
    public ResponseEntity<Map<String, Object>> getDashboard(
            @RequestParam(required = false) Integer day,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        try {
            // Nếu day, month, hoặc year là null, mặc định sẽ sử dụng ngày hiện tại
            if (day == null && month == null && year == null) {
                LocalDate currentDate = LocalDate.now();
                day = currentDate.getDayOfMonth();
                month = currentDate.getMonthValue();
                year = currentDate.getYear();
            }

            // Lấy dữ liệu từ service dựa trên các tham số ngày, tháng, năm đã xử lý
            Map<String, Object> dashboard = dashboardService.getDashboard(day, month, year);
            return ResponseEntity.ok(dashboard);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An error occurred"));
        }
    }


    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserData(
            @RequestParam(required = false) Integer day,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        try {
            Map<String, Object> response = dashboardService.getUserData(day, month, year);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while fetching user data"));
        }
    }


    @GetMapping("/transaction")
    public ResponseEntity<Map<String, Object>> getTransactionData(
            @RequestParam(required = false) Integer day,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        try {
            Map<String, Object> response = dashboardService.getTransactionData(day, month, year);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while fetching transaction data"));
        }
    }

    @GetMapping("/auction")
    public ResponseEntity<Map<String, Object>> getAuctionData(
            @RequestParam(required = false) Integer day,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        try {
            Map<String, Object> response = dashboardService.getAuctionData(day, month, year);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while fetching auction data"));
        }
    }
}
