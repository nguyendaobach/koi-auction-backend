package fall24.swp391.g1se1868.koiauction.repository;

import fall24.swp391.g1se1868.koiauction.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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



}

