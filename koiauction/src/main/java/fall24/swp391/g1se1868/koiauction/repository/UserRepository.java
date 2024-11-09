package fall24.swp391.g1se1868.koiauction.repository;

import fall24.swp391.g1se1868.koiauction.model.User;
import fall24.swp391.g1se1868.koiauction.model.UserAuctionCount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUserName(String userName);
    User findByEmail(String email);
    User findByPhoneNumber(String phone);

    @Transactional
    @Modifying
    @Query("update User u set u.password = ?2 where u.email = ?1")
    void updatePassword(String email, String encodePassword);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.status = 'UnActive', u.updateAt = CURRENT_TIMESTAMP WHERE u.id = ?1")
    void banUser(Integer id);


    @Transactional
    @Modifying
    @Query("update User u set u.status = 'Active' , u.updateAt = CURRENT_TIMESTAMP where u.id = ?1")
    void activeUser(Integer id);

    @Query("SELECT COUNT(u) FROM User u WHERE u.createAt BETWEEN :start AND :end")
    int countUsersByMonth(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT COUNT(u) FROM User u WHERE " +
            "(:day IS NULL OR FUNCTION('DAY', u.createAt) = :day) " +
            "AND (:month IS NULL OR FUNCTION('MONTH', u.createAt) = :month) " +
            "AND (:year IS NULL OR FUNCTION('YEAR', u.createAt) = :year)")
    int countNewUsers(@Param("day") Integer day,
                      @Param("month") Integer month,
                      @Param("year") Integer year);

    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status AND "
            + "(:day IS NULL OR FUNCTION('DAY', u.createAt) = :day) AND "
            + "(:month IS NULL OR FUNCTION('MONTH', u.createAt) = :month) AND "
            + "(:year IS NULL OR FUNCTION('YEAR', u.createAt) = :year)")
    Long countByStatus(@Param("status") String status,
                       @Param("day") Integer day,
                       @Param("month") Integer month,
                       @Param("year") Integer year);


    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND "
            + "(:day IS NULL OR FUNCTION('DAY', u.createAt) = :day) AND "
            + "(:month IS NULL OR FUNCTION('MONTH', u.createAt) = :month) AND "
            + "(:year IS NULL OR FUNCTION('YEAR', u.createAt) = :year)")
    Long countByRole(@Param("role") String role,
                     @Param("day") Integer day,
                     @Param("month") Integer month,
                     @Param("year") Integer year);

    @Query("SELECT new fall24.swp391.g1se1868.koiauction.model.UserAuctionCount(u.id, u.fullName, COUNT(a)) " +
            "FROM User u JOIN Auction a ON u.id = a.breederID " +
            "WHERE u.role = 'Breeder' AND a.status <> 'Reject' " +  // thêm khoảng trắng
            "GROUP BY u.id, u.fullName " +
            "ORDER BY COUNT(a) DESC")
    List<UserAuctionCount> findTopBreedersByAuctionCount(Pageable pageable);

}

