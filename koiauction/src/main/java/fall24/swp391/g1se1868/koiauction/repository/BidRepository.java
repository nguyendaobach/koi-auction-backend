package fall24.swp391.g1se1868.koiauction.repository;

import fall24.swp391.g1se1868.koiauction.model.Bid;
import fall24.swp391.g1se1868.koiauction.model.BidResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    @Query("SELECT b FROM Bid b WHERE b.auctionID.id = :auctionID order by b.id.time desc")
    List<Bid> findByAuctionID(@Param("auctionID") Integer auctionID);

    @Query("SELECT MAX(b.amount) FROM Bid b WHERE b.auctionID.id = :auctionId")
    Optional<Long> findHighestBidByAuctionId(@Param("auctionId") Integer auctionId);

    @Query("SELECT new fall24.swp391.g1se1868.koiauction.model.BidResponseDTO(a.id.auctionID, a.id.bidderID, a.id.time, a.amount, u.fullName,u.userName) " +
            "FROM Bid a JOIN a.bidderID u WHERE a.auctionID.id = :auctionId")
    List<BidResponseDTO> findBidsWithUserDetails(@Param("auctionId") Integer auctionId);

    @Query("SELECT COUNT(b) > 0 FROM Bid b WHERE b.id.auctionID = :auctionId AND b.bidderID.id = :userId")
    boolean existsByAuctionIdAndBidderId(@Param("auctionId") int auctionId, @Param("userId") int userId);
}
