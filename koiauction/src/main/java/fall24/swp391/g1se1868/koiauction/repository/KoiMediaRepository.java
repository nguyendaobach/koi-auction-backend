package fall24.swp391.g1se1868.koiauction.repository;

import fall24.swp391.g1se1868.koiauction.model.KoiFish;
import fall24.swp391.g1se1868.koiauction.model.KoiMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KoiMediaRepository extends JpaRepository<KoiMedia,Integer> {
    Optional<KoiMedia> findByKoiIDAndMediaType(KoiFish koiID, String mediaType);
    List<KoiMedia> findByKoiID(KoiFish koiID);
}
