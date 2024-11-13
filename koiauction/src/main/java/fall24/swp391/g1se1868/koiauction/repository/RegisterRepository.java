package fall24.swp391.g1se1868.koiauction.repository;

import fall24.swp391.g1se1868.koiauction.model.Register;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisterRepository extends JpaRepository<Register, Long> {
    Register findByEmail(String email);
}
