package fall24.swp391.g1se1868.koiauction.repository;

import fall24.swp391.g1se1868.koiauction.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserName(String userName);
    User findByEmail(String email);
    User findByPhoneNumber(String phone);

}

