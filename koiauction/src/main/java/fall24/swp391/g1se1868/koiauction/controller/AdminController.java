package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.StringResponse;
import fall24.swp391.g1se1868.koiauction.model.Transaction;
import fall24.swp391.g1se1868.koiauction.model.Wallet;
import fall24.swp391.g1se1868.koiauction.repository.TransactionRepository;
import fall24.swp391.g1se1868.koiauction.repository.WalletRepository;
import fall24.swp391.g1se1868.koiauction.service.StatisticsService;
import fall24.swp391.g1se1868.koiauction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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


    @GetMapping("/transaction")
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();

        return transactions;
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

    @GetMapping("/transaction/by-amount")
    public List<Transaction> getTransactionsByAmount(@RequestParam Long amount) {
        return transactionService.getTransactionsByAmount(amount);
    }

    @GetMapping("/transaction/by-time-range")
    public List<Transaction> getTransactionsByTimeRange(
            @RequestParam(name = "startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startTime,
            @RequestParam(name = "endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endTime) {

        // Chuyển đổi LocalDate thành Instant, lấy giờ đầu ngày cho startTime và giờ cuối ngày cho endTime
        Instant startInstant = startTime.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endInstant = endTime.atTime(23, 59, 59).atZone(ZoneOffset.UTC).toInstant();

        return transactionService.getTransactionsByDateRange(startInstant, endInstant);
    }

    @GetMapping("/transaction/by-type")
    public List<Transaction> getTransactionsByType(@RequestParam String transactionType) {
        return transactionService.getTransactionsByType(transactionType);
    }

    @GetMapping("/total-balance")
    public Long getTotalBalance() {
        return walletRepository.getTotalBalance();
    }

    @GetMapping("/wallet")
    public List<Wallet> wallets(){
        return walletRepository.findAll();
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
