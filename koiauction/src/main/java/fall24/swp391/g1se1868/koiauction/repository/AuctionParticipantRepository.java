package fall24.swp391.g1se1868.koiauction.repository;

import fall24.swp391.g1se1868.koiauction.model.Auction;
import fall24.swp391.g1se1868.koiauction.model.AuctionParticipant;
import fall24.swp391.g1se1868.koiauction.model.AuctionParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AuctionParticipantRepository extends JpaRepository<AuctionParticipant, AuctionParticipantId> {
    @Query("SELECT ap.auctionID.id FROM AuctionParticipant ap WHERE ap.userID.id = :userID AND ap.status='deposited'")
    List<Integer> findAuctionIdsByUserId(Integer userID);


    @Query("SELECT a FROM Auction a WHERE a.id IN :auctionIds")
    List<Auction> findAuctionsByIds(List<Integer> auctionIds);
}
