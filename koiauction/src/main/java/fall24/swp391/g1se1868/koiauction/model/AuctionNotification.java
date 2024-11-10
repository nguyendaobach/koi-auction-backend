package fall24.swp391.g1se1868.koiauction.model;

public class AuctionNotification {
        private String status;  // 'winner', 'loser', 'status'
        private Integer winnerId; // ID của người nhận thông báo, có thể null nếu là thông báo chung
        private Long amount;

    public AuctionNotification(String status, Integer winnerId, Long amount) {
        this.status = status;
        this.winnerId = winnerId;
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Integer winnerId) {
        this.winnerId = winnerId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
