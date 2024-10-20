package fall24.swp391.g1se1868.koiauction.repository;

import fall24.swp391.g1se1868.koiauction.model.KoiFish;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface KoiFishRepository extends JpaRepository<KoiFish,Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE KoiFish k SET k.status = 'Removed' WHERE k.id = :id")
    void delete(Integer id);
}
