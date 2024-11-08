package fall24.swp391.g1se1868.koiauction.model;

public class BidRequest {
    private Integer auctionId;
    private Long amount;

    public Integer getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Integer auctionId) {
        this.auctionId = auctionId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
