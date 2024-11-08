package fall24.swp391.g1se1868.koiauction.model;

public class AuctionResponse {
    private String message;
    private Integer AuctionId;

    public AuctionResponse(String message, Integer auctionId) {
        this.message = message;
        this.AuctionId = auctionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getAuctionId() {
        return AuctionId;
    }

    public void setAuctionId(Integer auctionId) {
        AuctionId = auctionId;
    }
}
