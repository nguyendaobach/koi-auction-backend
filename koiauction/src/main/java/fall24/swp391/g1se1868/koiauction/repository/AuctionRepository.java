package fall24.swp391.g1se1868.koiauction.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import fall24.swp391.g1se1868.koiauction.model.Auction;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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


    Page<Auction> findAll(Pageable pageable);
}
