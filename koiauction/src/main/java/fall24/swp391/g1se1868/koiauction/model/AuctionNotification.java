package fall24.swp391.g1se1868.koiauction.model;

public class AuctionNotification {
        private String type;  // 'winner', 'loser', 'status'
        private Integer userId; // ID của người nhận thông báo, có thể null nếu là thông báo chung
        private String message;

        public AuctionNotification(String type, Integer userId, String message) {
            this.type = type;
            this.userId = userId;
            this.message = message;
        }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
