package fall24.swp391.g1se1868.koiauction.repository;

import fall24.swp391.g1se1868.koiauction.model.AuctionKoi;
import fall24.swp391.g1se1868.koiauction.model.KoiFish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionKoiRepository extends JpaRepository<KoiFish, Integer> {
    @Query("SELECT ak.koiID FROM AuctionKoi ak WHERE ak.auctionID.id = :auctionId")
    List<KoiFish> findKoiFishByAuctionId(@Param("auctionId") Integer auctionId);

    public AuctionKoi save(AuctionKoi auctionKoi);
}
