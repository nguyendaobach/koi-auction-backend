package fall24.swp391.g1se1868.koiauction.repository;

import fall24.swp391.g1se1868.koiauction.model.Chat;
import fall24.swp391.g1se1868.koiauction.model.UserChat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
    @Query("SELECT cm FROM Chat cm WHERE (cm.senderId = :senderId AND cm.receiverId = :receiverId) " +
            "OR (cm.senderId = :receiverId AND cm.receiverId = :senderId) ORDER BY cm.datetime ASC")
    Page<Chat> findMessages(Integer senderId, Integer receiverId, Pageable pageable);


    @Query("SELECT new fall24.swp391.g1se1868.koiauction.model.UserChat(u.id, u.fullName, u.role) " +
            "FROM User u " +
            "JOIN Chat c ON (c.senderId = :senderId AND c.receiverId = u.id) " +
            "OR (c.receiverId = :senderId AND c.senderId = u.id) " +
            "GROUP BY u.id, u.fullName, u.role")
    List<UserChat> getUserChatsBySenderId(@Param("senderId") Integer senderId);


    @Query("SELECT new fall24.swp391.g1se1868.koiauction.model.UserChat(u.id, u.fullName, u.role) " +
            "FROM User u " +
            "WHERE u.role in ('STAFF','ADMIN','BREEDER') ")
    List<UserChat> getStaffToChat();
}
