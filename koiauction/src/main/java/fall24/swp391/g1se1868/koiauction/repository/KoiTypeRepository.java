package fall24.swp391.g1se1868.koiauction.repository;

import fall24.swp391.g1se1868.koiauction.model.KoiType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KoiTypeRepository extends JpaRepository<KoiType, Integer> {
    // Bạn có thể thêm các phương thức truy vấn tùy chỉnh tại đây nếu cần
}
