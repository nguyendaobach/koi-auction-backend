package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.Chat;
import fall24.swp391.g1se1868.koiauction.model.UserChat;
import fall24.swp391.g1se1868.koiauction.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatMessageRepository;

    public Chat saveMessage(Chat message) {
        message.setDatetime(LocalDateTime.now());
        return chatMessageRepository.save(message);
    }

    public Page<Chat> getChatMessages(Integer senderId, Integer receiverId, int page) {
        Pageable pageable = PageRequest.of(page, 20);
        return chatMessageRepository.findMessages(senderId, receiverId, pageable);
    }

    public List<UserChat> getUserChats(Integer userId) {
        return chatMessageRepository.getUserChatsBySenderId(userId);
    }

    public List<UserChat> getStaffChat(){
        return chatMessageRepository.getStaffToChat();
    }
}

