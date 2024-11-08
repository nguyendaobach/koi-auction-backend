package fall24.swp391.g1se1868.koiauction.model;

import jakarta.persistence.*;


@Entity
public class Bid {
    @EmbeddedId
    private BidId id;

    @MapsId("auctionID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AuctionID", nullable = false)
    private Auction auctionID;

    @MapsId("bidderID")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "BidderID")
    private User bidderID;

    @Column(name = "Amount", precision = 10, scale = 2)
    private  Long amount;

    public BidId getId() {
        return id;
    }

    public void setId(BidId id) {
        this.id = id;
    }

    public Auction getAuctionID() {
        return auctionID;
    }

    public void setAuctionID(Auction auctionID) {
        this.auctionID = auctionID;
    }

    public User getBidderID() {
        return bidderID;
    }

    public void setBidderID(User bidderID) {
        this.bidderID = bidderID;
    }

    public  Long getAmount() {
        return amount;
    }

    public void setAmount( Long amount) {
        this.amount = amount;
    }

}