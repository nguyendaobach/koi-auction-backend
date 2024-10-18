package fall24.swp391.g1se1868.koiauction.service;
import fall24.swp391.g1se1868.koiauction.model.Transaction;
import fall24.swp391.g1se1868.koiauction.repository.TransactionRepository;
import fall24.swp391.g1se1868.koiauction.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    // Lấy tất cả giao dịch
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    // Lấy giao dịch theo ID
    public Transaction getTransactionById(Integer id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }
    // Lấy giao dịch theo số tiền (Amount)
    public List<Transaction> getTransactionsByAmount(Long amount) {
        return transactionRepository.findByAmount(amount);
    }
    // Tìm giao dịch giữa hai mốc thời gian
    public List<Transaction> getTransactionsByDateRange(Instant startTime, Instant endTime) {
        return transactionRepository.findByTimeBetween(startTime, endTime);
    }
    // Lấy giao dịch theo loại giao dịch (TransactionType)
    public List<Transaction> getTransactionsByType(String transactionType) {
        return transactionRepository.findByTransactionType(transactionType);
    }
}
