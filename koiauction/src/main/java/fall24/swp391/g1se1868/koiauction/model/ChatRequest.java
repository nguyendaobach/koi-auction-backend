package fall24.swp391.g1se1868.koiauction.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

public class ChatRequest {
    private Integer receiverId;
    private String message;

    public ChatRequest( Integer receiverId, String message) {
        this.receiverId = receiverId;
        this.message = message;
    }

    public ChatRequest() {
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
}
