package fall24.swp391.g1se1868.koiauction.repository;

import fall24.swp391.g1se1868.koiauction.model.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
    @Query("SELECT cm FROM Chat cm WHERE (cm.senderId = :senderId AND cm.receiverId = :receiverId) " +
            "OR (cm.senderId = :receiverId AND cm.receiverId = :senderId) ORDER BY cm.datetime ASC")
    Page<Chat> findMessages(Integer senderId, Integer receiverId, Pageable pageable);
}
