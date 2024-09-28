package fall24.swp391.g1se1868.koiauction.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AuctionParticipantId implements Serializable {
    private static final long serialVersionUID = 3722606563665790960L;
    @Column(name = "AuctionID", nullable = false)
    private Integer auctionID;

    @Column(name = "UserID", nullable = false)
    private Integer userID;

    public Integer getAuctionID() {
        return auctionID;
    }

    public void setAuctionID(Integer auctionID) {
        this.auctionID = auctionID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AuctionParticipantId entity = (AuctionParticipantId) o;
        return Objects.equals(this.auctionID, entity.auctionID) &&
                Objects.equals(this.userID, entity.userID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(auctionID, userID);
    }

}