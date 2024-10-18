package fall24.swp391.g1se1868.koiauction.repository;

import fall24.swp391.g1se1868.koiauction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Query("SELECT t FROM Transaction t WHERE t.walletID.id = ?1")
    List<Transaction> findByWalletID(Integer walletID);

    List<Transaction> findByAmount(Long amount);
    // Tìm giao dịch giữa hai mốc thời gian
    List<Transaction> findByTimeBetween(Instant startTime, Instant endTime);
    List<Transaction> findByTransactionType(String transactionType);

}
