package fall24.swp391.g1se1868.koiauction.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Bid {
    @EmbeddedId
    private BidId id;

    @MapsId("auctionID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AuctionID", nullable = false)
    private Auction auctionID;

    @MapsId("bidderID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BidderID", nullable = false)
    private User bidderID;

    @Column(name = "Amount", precision = 10, scale = 2)
    private BigDecimal amount;

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}