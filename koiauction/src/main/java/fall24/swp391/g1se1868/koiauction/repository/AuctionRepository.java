package fall24.swp391.g1se1868.koiauction.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import fall24.swp391.g1se1868.koiauction.model.Auction;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
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

    @Query("SELECT a FROM Auction a WHERE a.startTime > CURRENT_TIMESTAMP AND a.status = 'Pending'")
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

//    @Query("SELECT a FROM Auction a WHERE (?1 IS NULL OR a.status IN ?1) " +
//            "AND (?2 IS NULL OR a.auctionMethod IN ?2) " +
//            "AND a.status <> 'Pending' " +
//            "ORDER BY " +
//            "CASE WHEN ?3 = 'DESC' THEN a.startTime END DESC, " +
//            "CASE WHEN ?3 <> 'DESC' THEN a.startTime END ASC")
//    Page<Auction> findAll(List<String> status, List<String> method, String desc, Pageable pageable);


    @Query("SELECT a FROM Auction a WHERE (:status IS NULL OR a.status IN :status) " +
            "AND (:method IS NULL OR a.auctionMethod IN :method) " +
            "AND a.status <> 'Pending' " +
            "AND a.status <> 'Reject' " +
            "ORDER BY " +
            "CASE WHEN :desc = 'DESC' THEN a.startTime END DESC, " +
            "CASE WHEN :desc <> 'DESC' THEN a.startTime END ASC")
    Page<Auction> findAll(@Param("status") List<String> status,
                          @Param("method") List<String> method,
                          @Param("desc") String desc,
                          Pageable pageable);


    @Query("SELECT a FROM Auction a")
    Page<Auction> findAllAdmin(Pageable pageable);
}
