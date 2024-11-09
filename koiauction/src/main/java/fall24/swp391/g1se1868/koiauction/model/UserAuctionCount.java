package fall24.swp391.g1se1868.koiauction.model;

public class UserAuctionCount {
    private Integer userId;
    private String fullName;
    private Long auctionCount;

    public UserAuctionCount(Integer userId, String fullName, Long auctionCount) {
        this.userId = userId;
        this.fullName = fullName;
        this.auctionCount = auctionCount;
    }

    // Getters và Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getAuctionCount() {
        return auctionCount;
    }

    public void setAuctionCount(Long auctionCount) {
        this.auctionCount = auctionCount;
    }
}
