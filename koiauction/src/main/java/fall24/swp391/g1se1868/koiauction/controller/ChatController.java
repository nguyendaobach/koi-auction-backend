package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.Chat;
import fall24.swp391.g1se1868.koiauction.model.ChatRequest;
import fall24.swp391.g1se1868.koiauction.model.UserPrinciple;
import fall24.swp391.g1se1868.koiauction.service.ChatService;// Assuming you have this custom UserPrinciple class
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void sendMessage(ChatRequest message) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        Chat chat = new Chat();
        chat.setMessage(message.getMessage());
        chat.setDatetime(message.getDatetime());
        chat.setSenderId(userPrinciple.getId());
        chat.setReceiverId(message.getReceiverId());
        Chat savedMessage = chatService.saveMessage(chat);
        messagingTemplate.convertAndSend("/topic/messages/" + message.getReceiverId(), savedMessage);
    }

    @GetMapping("/messages")
    public Page<Chat> getChatMessages(
            @RequestParam Integer receiverId,
            @RequestParam int page) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        Integer senderId = userPrinciple.getId();
        return chatService.getChatMessages(senderId, receiverId, page);
    }
}

