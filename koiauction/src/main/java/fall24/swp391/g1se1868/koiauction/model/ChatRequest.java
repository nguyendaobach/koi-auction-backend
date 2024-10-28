package fall24.swp391.g1se1868.koiauction.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

public class ChatRequest {
    private Integer chatId;
    private Integer receiverId;
    private String message;
    private LocalDateTime datetime = LocalDateTime.now();

    public ChatRequest(Integer chatId, Integer receiverId, String message, LocalDateTime datetime) {
        this.chatId = chatId;
        this.receiverId = receiverId;
        this.message = message;
        this.datetime = datetime;
    }

    public ChatRequest() {
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }
}
