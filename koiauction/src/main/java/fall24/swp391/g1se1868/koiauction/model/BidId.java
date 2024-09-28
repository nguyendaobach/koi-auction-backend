package fall24.swp391.g1se1868.koiauction.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Embeddable
public class BidId implements Serializable {
    private static final long serialVersionUID = 1949232659821234949L;
    @Column(name = "AuctionID", nullable = false)
    private Integer auctionID;

    @Column(name = "BidderID", nullable = false)
    private Integer bidderID;

    @Column(name = "\"Time\"", nullable = false)
    private Instant time;

    public Integer getAuctionID() {
        return auctionID;
    }

    public void setAuctionID(Integer auctionID) {
        this.auctionID = auctionID;
    }

    public Integer getBidderID() {
        return bidderID;
    }

    public void setBidderID(Integer bidderID) {
        this.bidderID = bidderID;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BidId entity = (BidId) o;
        return Objects.equals(this.auctionID, entity.auctionID) &&
                Objects.equals(this.bidderID, entity.bidderID) &&
                Objects.equals(this.time, entity.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(auctionID, bidderID, time);
    }

}