package fall24.swp391.g1se1868.koiauction.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import fall24.swp391.g1se1868.koiauction.model.Auction;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Integer> {
    @Query("SELECT a FROM Auction a WHERE a.startTime > CURRENT_TIMESTAMP AND a.status = 'Scheduled'")
    Page<Auction> findOnScheduleAuctions(Pageable pageable);

    @Query("SELECT a FROM Auction a WHERE a.startTime <= CURRENT_TIMESTAMP AND a.endTime > CURRENT_TIMESTAMP AND a.status = 'Ongoing'")
    Page<Auction> findOngoingAuctions(Pageable pageable);

    @Query("SELECT a FROM Auction a WHERE a.status = 'Pending'")
    Page<Auction> getAllAuctionRequest(Pageable pageable);


    @Transactional
    @Query("SELECT a, u.fullName AS winnerName " +
            "FROM Auction a " +
            "JOIN User u ON a.winnerID = u.id " +
            "WHERE a.endTime < CURRENT_TIMESTAMP " +
            "AND a.status = 'Closed'")
    Page<Object[]> findPastAuctionsWithWinnerName(Pageable pageable);

    @Query("SELECT a FROM Auction a WHERE a.winnerID = ?1")
    Page<Auction> getAuctionbyWinnerID(int winnerID, Pageable pageable);

    @Query("SELECT DISTINCT a FROM Auction a " +
            "INNER JOIN AuctionKoi ak ON a.id = ak.id.auctionID " +
            "INNER JOIN KoiFish k ON ak.koiID.id = k.id " +
            "WHERE (COALESCE(:status, NULL) IS NULL OR a.status IN :status) " +
            "AND (COALESCE(:method, NULL) IS NULL OR a.auctionMethod IN :method) " +
            "AND a.status <> 'Pending' " +
            "AND a.status <> 'Reject' " +
            "ORDER BY a.startTime DESC")
    Page<Auction> findAllDesc(@Param("status") List<String> status,
                              @Param("method") List<String> method,
                              Pageable pageable);

    @Query("SELECT DISTINCT a FROM Auction a " +
            "INNER JOIN AuctionKoi ak ON a.id = ak.id.auctionID " +
            "INNER JOIN KoiFish k ON ak.koiID.id = k.id " +
            "WHERE (COALESCE(:status, NULL) IS NULL OR a.status IN :status) " +
            "AND (COALESCE(:method, NULL) IS NULL OR a.auctionMethod IN :method) " +
            "AND a.status <> 'Pending' " +
            "AND a.status <> 'Reject' " +
            "ORDER BY a.startTime ASC")
    Page<Auction> findAllAsc(@Param("status") List<String> status,
                             @Param("method") List<String> method,
                             Pageable pageable);


    @Query("SELECT DISTINCT a FROM Auction a " +
            "INNER JOIN AuctionKoi ak ON a.id = ak.id.auctionID " +
            "INNER JOIN KoiFish k ON ak.koiID.id = k.id " +
            "WHERE (COALESCE(:status, NULL) IS NULL OR a.status IN :status) " +
            "AND (COALESCE(:method, NULL) IS NULL OR a.auctionMethod IN :method) " +
            "AND a.breederID = :BreederId " +
            "ORDER BY a.startTime DESC")
    Page<Auction> findAllOwnerDesc(@Param("status") List<String> status,
                              @Param("method") List<String> method,
                              @Param("BreederId") Integer BreederId,
                              Pageable pageable);

    @Query("SELECT DISTINCT a FROM Auction a " +
            "INNER JOIN AuctionKoi ak ON a.id = ak.id.auctionID " +
            "INNER JOIN KoiFish k ON ak.koiID.id = k.id " +
            "WHERE (COALESCE(:status, NULL) IS NULL OR a.status IN :status) " +
            "AND (COALESCE(:method, NULL) IS NULL OR a.auctionMethod IN :method) " +
            "AND a.breederID = :BreederId " +
            "ORDER BY a.startTime ASC")
    Page<Auction> findAllOwnerAsc(@Param("status") List<String> status,
                             @Param("method") List<String> method,
                                  @Param("BreederId") Integer BreederId,
                             Pageable pageable);

    @Query("SELECT a FROM Auction a")
    Page<Auction> findAllAdmin(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Auction k SET k.status = 'Cancelled' WHERE k.id = :id")
    String delete(@Param("id") Integer id);

    @Query(value = "SELECT SUM(a.AuctionFee) FROM Auction a WHERE a.status = 'Closed' AND YEAR(a.endTime) = :year", nativeQuery = true)
    Long getRevenueByYear(@Param("year") int year);

    @Query(value = "SELECT SUM(a.AuctionFee) FROM Auction a WHERE a.status = 'Closed' AND MONTH(a.endTime) = :month AND YEAR(a.endTime) = :year", nativeQuery = true)
    Long getRevenueByMonth(@Param("month") int month, @Param("year") int year);

    @Query(value = "SELECT SUM(a.AuctionFee) FROM Auction a WHERE a.status = 'Closed' AND DAY(a.endTime) = :day AND MONTH(a.endTime) = :month AND YEAR(a.endTime) = :year", nativeQuery = true)
    Long getRevenueByDay(@Param("day") int day, @Param("month") int month, @Param("year") int year);

    @Query(value = "SELECT SUM(a.AuctionFee) FROM Auction a WHERE a.status = 'Closed'", nativeQuery = true)
    Long getTotalRevenue();

    @Query("SELECT COUNT(a) FROM Auction a WHERE FUNCTION('DAY', a.startTime) = :day AND FUNCTION('MONTH', a.startTime) = :month AND FUNCTION('YEAR', a.startTime) = :year AND a.status = 'Ongoing'")
    Long getCountAuctionByDay(@Param("day") int day, @Param("month") int month, @Param("year") int year);

    // Count auctions for a specific month based on startTime
    @Query("SELECT COUNT(a) FROM Auction a WHERE FUNCTION('MONTH', a.startTime) = :month AND FUNCTION('YEAR', a.startTime) = :year AND a.status = 'Ongoing'")
    Long getCountAuctionByMonth(@Param("month") int month, @Param("year") int year);

    // Count auctions for a specific year based on startTime
    @Query("SELECT COUNT(a) FROM Auction a WHERE FUNCTION('YEAR', a.startTime) = :year AND a.status = 'Ongoing'")
    Long getCountAuctionByYear(@Param("year") int year);

    // Count auctions for a specific day with Finished status
    @Query("SELECT COUNT(a) FROM Auction a WHERE FUNCTION('DAY', a.startTime) = :day AND FUNCTION('MONTH', a.startTime) = :month AND FUNCTION('YEAR', a.startTime) = :year AND a.status = :status")
    Long getCountAuctionByDayAndStatus(@Param("day") int day, @Param("month") int month, @Param("year") int year, @Param("status") String status);

    // Count auctions for a specific month with Finished status
    @Query("SELECT COUNT(a) FROM Auction a WHERE FUNCTION('MONTH', a.startTime) = :month AND FUNCTION('YEAR', a.startTime) = :year AND a.status = :status")
    Long getCountAuctionByMonthAndStatus(@Param("month") int month, @Param("year") int year, @Param("status") String status);

    // Count auctions for a specific year with Finished status
    @Query("SELECT COUNT(a) FROM Auction a WHERE FUNCTION('YEAR', a.startTime) = :year AND a.status = :status")
    Long getCountAuctionByYearAndStatus(@Param("year") int year, @Param("status") String status);


    @Query("SELECT COUNT(a) FROM Auction a WHERE a.status = :status")
    Long getTotalFinishedAuctionCount(@Param("status") String status);


    @Query("SELECT COUNT(a) FROM Auction a WHERE a.status = 'Ongoing'")
    Long getTotalAuctionCount();


    @Query("SELECT a FROM Auction a " +
            "JOIN AuctionKoi ak ON a.id = ak.auctionID.id " +
            "JOIN KoiFish k ON ak.koiID.id = k.id " +
            "WHERE LOWER(k.koiName) LIKE LOWER(CONCAT('%', :koiName, '%'))")

    Page<Auction> findAuctionsByKoiNameContaining(@Param("koiName") String koiName,Pageable pageable);



}
