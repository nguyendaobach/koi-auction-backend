package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.StringResponse;
import fall24.swp391.g1se1868.koiauction.model.Transaction;
import fall24.swp391.g1se1868.koiauction.model.Wallet;
import fall24.swp391.g1se1868.koiauction.repository.TransactionRepository;
import fall24.swp391.g1se1868.koiauction.repository.WalletRepository;
import fall24.swp391.g1se1868.koiauction.service.StatisticsService;
import fall24.swp391.g1se1868.koiauction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private StatisticsService statisticsService;


    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchTransactions(
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endTime,
            @RequestParam(required = false) Long walletID,
            @RequestParam(required = false) Long amountStart,
            @RequestParam(required = false) Long amountEnd,
            @PageableDefault(size = 10) Pageable pageable) {

        // Chuyển đổi thời gian startTime và endTime sang Instant
        Instant startInstant = (startTime != null) ? startTime.atStartOfDay(ZoneOffset.UTC).toInstant() : null;
        Instant endInstant = (endTime != null) ? endTime.atTime(23, 59, 59).atZone(ZoneOffset.UTC).toInstant() : null;

        // Lấy kết quả tìm kiếm từ service
        Page<Transaction> transactions = transactionService.searchTransactions(
                transactionType, startInstant, endInstant, walletID, amountStart, amountEnd, pageable);

        // Tạo đối tượng response trả về
        Map<String, Object> response = new HashMap<>();
        response.put("transactions", transactions.getContent());  // Các giao dịch
        response.put("currentPage", transactions.getNumber());  // Trang hiện tại
        response.put("totalPages", transactions.getTotalPages());  // Tổng số trang
        response.put("totalElements", transactions.getTotalElements());  // Tổng số phần tử

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/transaction/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Integer id) {
        System.out.println("Requested transaction ID: " + id); // Ghi lại ID được yêu cầu
        Transaction transaction = transactionService.getTransactionById(id);

        if (transaction != null) {
            return ResponseEntity.ok(transaction);
        }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new StringResponse("Giao dịch không tìm thấy"));

    }


    @GetMapping("/total-balance")
    public Long getTotalBalance() {
        return walletRepository.getTotalBalance();
    }

    @GetMapping("/wallet")
    public ResponseEntity<Map<String, Object>> wallets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable=PageRequest.of(page,size);

        Page<Wallet> page1= walletRepository.findAll(pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("auctions", page1.getContent());
        response.put("currentPage", page1.getNumber()); // Trang hiện tại
        response.put("totalPages", page1.getTotalPages()); // Tổng số trang
        response.put("totalElements", page1.getTotalElements());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/wallet/{id}")
    public Optional<Wallet> walletss(@PathVariable Integer id){
        return walletRepository.findById(id);
    }
    @GetMapping("/statistics/users-per-month")
    public ResponseEntity<Map<String, Integer>> getUsersPerMonth() {
        Map<String, Integer> usersPerMonth = statisticsService.getUsersPerMonth();
        return ResponseEntity.ok(usersPerMonth);
    }


    @GetMapping("/statistics/transactions-per-month")
    public ResponseEntity<Map<String, Integer>> getTransactionsPerMonth() {
        Map<String, Integer> transactionsPerMonth = statisticsService.getTransactionsPerMonth();
        return ResponseEntity.ok(transactionsPerMonth);
    }

}
