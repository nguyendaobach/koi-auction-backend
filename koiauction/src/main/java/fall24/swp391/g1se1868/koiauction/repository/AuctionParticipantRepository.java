package fall24.swp391.g1se1868.koiauction.repository;

import fall24.swp391.g1se1868.koiauction.model.Auction;
import fall24.swp391.g1se1868.koiauction.model.AuctionParticipant;
import fall24.swp391.g1se1868.koiauction.model.AuctionParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AuctionParticipantRepository extends JpaRepository<AuctionParticipant, AuctionParticipantId> {
    @Query("SELECT ap.auctionID.id FROM AuctionParticipant ap WHERE ap.userID.id = :userID AND ap.status='deposited'")
    List<Integer> findAuctionIdsByUserId(Integer userID);


    @Query("SELECT a FROM Auction a WHERE a.id IN :auctionIds")
    List<Auction> findAuctionsByIds(List<Integer> auctionIds);


    @Query("SELECT CASE WHEN COUNT(ap) > 0 THEN true ELSE false END " +
            "FROM AuctionParticipant ap " +
            "WHERE ap.userID.id = ?1 AND ap.auctionID.id = ?2")
    boolean existsByUserIdAndAuctionId( int userId, int auctionId);
}
