package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.Chat;
import fall24.swp391.g1se1868.koiauction.model.ChatRequest;
import fall24.swp391.g1se1868.koiauction.model.UserChat;
import fall24.swp391.g1se1868.koiauction.model.UserPrinciple;
import fall24.swp391.g1se1868.koiauction.service.ChatService;// Assuming you have this custom UserPrinciple class
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/chat")
    public ResponseEntity<?> sendMessage(@RequestBody ChatRequest message) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        if (message.getMessage() == null || message.getMessage().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        Chat chat = new Chat();
        chat.setMessage(message.getMessage());
        chat.setDatetime(LocalDateTime.now());
        chat.setSenderId(userPrinciple.getId());
        chat.setReceiverId(message.getReceiverId());
        Chat savedMessage = chatService.saveMessage(chat);
        String chatRoomId = "room-" + Math.min(userPrinciple.getId(), message.getReceiverId()) + "-" + Math.max(userPrinciple.getId(), message.getReceiverId());
        messagingTemplate.convertAndSend("/topic/" + chatRoomId, savedMessage);
        return ResponseEntity.ok(savedMessage);
    }


    @GetMapping("/messages")
    public List<Chat> getChatMessages(
            @RequestParam Integer receiverId,
            @RequestParam(defaultValue = "0") int page) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        Integer senderId = userPrinciple.getId();
        return chatService.getChatMessages(senderId, receiverId, page);
    }

    @GetMapping("/userchat")
    public List<UserChat> getUserChats(@RequestParam Integer userId) {
        return chatService.getUserChats(userId);
    }

    @GetMapping("/systemchat")
    public List<UserChat> getStaffChats() {
        return chatService.getStaffChat();
    }
}

