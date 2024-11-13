package fall24.swp391.g1se1868.koiauction.repository;

import fall24.swp391.g1se1868.koiauction.model.Order;
import fall24.swp391.g1se1868.koiauction.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {


    @Query("SELECT o FROM Order o WHERE o.bidderID.id = :bidderId")
    List<Order> findOrdersByBidderId(@Param("bidderId") Integer bidderId);

    @Query("SELECT o FROM Order o WHERE o.auctionID.breederID = :breederId")
    List<Order> findOrdersByBreederId(@Param("breederId") Integer breederId);

    @Query("SELECT o FROM Order o WHERE o.bidderID.id = :userId OR o.auctionID.breederID = :userId")
    List<Order> findOrdersByUserId(@Param("userId") Integer userId);
    @Query("SELECT o FROM Order o WHERE o.auctionID.id= :AuctionId")
    Order findOrderByAuctionId(@Param("AuctionId") Integer AuctionId);

    @Query("SELECT o FROM Order o WHERE o.auctionID.status = 'Dispute'")
    List<Order> findOrderByStatus();

}
