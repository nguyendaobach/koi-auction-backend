package fall24.swp391.g1se1868.koiauction.repository;

import fall24.swp391.g1se1868.koiauction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Query("SELECT t FROM Transaction t WHERE t.walletID.id = ?1")
    List<Transaction> findByWalletID(Integer walletID);
    List<Transaction> findByAmount(Long amount);
    List<Transaction> findByTimeBetween(Instant startTime, Instant endTime);
    List<Transaction> findByTransactionType(String transactionType);
    @Query("SELECT COUNT(t) FROM Transaction t WHERE MONTH(t.time) = :month AND YEAR(t.time) = :year")
    int countTransactionsByMonth(@Param("month") int month, @Param("year") int year);


    @Query("SELECT COUNT(t) FROM Transaction t WHERE "
            + "(:day IS NULL OR FUNCTION('DAY', t.time) = :day) AND "
            + "(:month IS NULL OR FUNCTION('MONTH', t.time) = :month) AND "
            + "(:year IS NULL OR FUNCTION('YEAR', t.time) = :year)")
    Long count(@Param("day") Integer day,
               @Param("month") Integer month,
               @Param("year") Integer year);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE "
            + "(:day IS NULL OR DAY(t.time) = :day) AND "
            + "(:month IS NULL OR MONTH(t.time) = :month) AND "
            + "(:year IS NULL OR YEAR(t.time) = :year)")
    Long countByDate(Integer day, Integer month, Integer year);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE "
            + "t.transactionType = :transactionType AND "
            + "(:day IS NULL OR DAY(t.time) = :day) AND "
            + "(:month IS NULL OR MONTH(t.time) = :month) AND "
            + "(:year IS NULL OR YEAR(t.time) = :year)")
    Long countByTransactionType(String transactionType, Integer day, Integer month, Integer year);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE "
            + "t.status = :status AND "
            + "(:day IS NULL OR DAY(t.time) = :day) AND "
            + "(:month IS NULL OR MONTH(t.time) = :month) AND "
            + "(:year IS NULL OR YEAR(t.time) = :year)")
    Long countByStatus(String status, Integer day, Integer month, Integer year);

    List<Transaction> findByAmountBetween(Long amountStart, Long amountEnd);

    List<Transaction> findByWalletID_Id(Long walletID);

    @Query("SELECT t FROM Transaction t " +
            "WHERE (:transactionType IS NULL OR t.transactionType = :transactionType) " +
            "AND (:startInstant IS NULL OR t.time >= :startInstant) " +
            "AND (:endInstant IS NULL OR t.time <= :endInstant) " +
            "AND (:walletID IS NULL OR t.walletID.id = :walletID) " +
            "AND (:amountStart IS NULL OR t.amount >= :amountStart) " +
            "AND (:amountEnd IS NULL OR t.amount <= :amountEnd)")
    List<Transaction> searchTransactions(
            @Param("transactionType") String transactionType,
            @Param("startInstant") Instant startInstant,
            @Param("endInstant") Instant endInstant,
            @Param("walletID") Long walletID,
            @Param("amountStart") Long amountStart,
            @Param("amountEnd") Long amountEnd);
}
