package fall24.swp391.g1se1868.koiauction.model;

import java.time.Instant;

public class BidResponseDTO {
    private Integer auctionId;
    private Integer bidderId;
    private Instant bidTime;
    private Long amount;
    private String fullName;

    // Constructor
        public BidResponseDTO(Integer auctionId, Integer bidderId, Instant bidTime, Long amount, String fullName) {
        this.auctionId = auctionId;
        this.bidderId = bidderId;
        this.bidTime = bidTime;
        this.amount = amount;
        this.fullName = fullName;
    }

    public BidResponseDTO() {
    }

    // Getters and setters
    public Integer getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Integer auctionId) {
        this.auctionId = auctionId;
    }

    public Integer getBidderId() {
        return bidderId;
    }

    public void setBidderId(Integer bidderId) {
        this.bidderId = bidderId;
    }

    public Instant getBidTime() {
        return bidTime;
    }

    public void setBidTime(Instant bidTime) {
        this.bidTime = bidTime;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}

