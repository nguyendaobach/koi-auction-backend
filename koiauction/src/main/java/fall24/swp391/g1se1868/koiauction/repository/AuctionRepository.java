package fall24.swp391.g1se1868.koiauction.repository;

import fall24.swp391.g1se1868.koiauction.model.Auction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionRepository {
    @Query("SELECT a FROM Auction a WHERE a.startTime > CURRENT_TIMESTAMP AND a.status = 'Scheduled'")
    List<Auction> findOnScheduleAuctions();

    @Query("SELECT a FROM Auction a WHERE a.startTime <= CURRENT_TIMESTAMP AND a.endTime > CURRENT_TIMESTAMP AND a.status = 'Ongoing'")
    List<Auction> findOngoingAuctions();

    @Query("SELECT a, u.fullName AS winnerName " +
            "FROM Auction a " +
            "JOIN User u ON a.winnerID = u.id " +
            "WHERE a.endTime < CURRENT_TIMESTAMP " +
            "AND a.status = 'Closed'")
    List<Object[]> findPastAuctionsWithWinnerName();




}
