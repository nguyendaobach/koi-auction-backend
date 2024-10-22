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
    List<Object[]> findPastAuctionsWithWinnerName();

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



    @Query("SELECT a FROM Auction a")
    List<Auction> findAllAdmin();

    @Modifying
    @Transactional
    @Query("UPDATE Auction k SET k.status = 'Cancelled' WHERE k.id = :id")
    String delete(@Param("id") Integer id);
}
