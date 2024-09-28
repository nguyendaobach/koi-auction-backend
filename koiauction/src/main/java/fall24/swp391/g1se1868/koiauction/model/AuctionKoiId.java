package fall24.swp391.g1se1868.koiauction.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AuctionKoiId implements Serializable {
    private static final long serialVersionUID = 7109108221125139071L;
    @Column(name = "AuctionID", nullable = false)
    private Integer auctionID;

    @Column(name = "KoiID", nullable = false)
    private Integer koiID;

    public Integer getAuctionID() {
        return auctionID;
    }

    public void setAuctionID(Integer auctionID) {
        this.auctionID = auctionID;
    }

    public Integer getKoiID() {
        return koiID;
    }

    public void setKoiID(Integer koiID) {
        this.koiID = koiID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AuctionKoiId entity = (AuctionKoiId) o;
        return Objects.equals(this.auctionID, entity.auctionID) &&
                Objects.equals(this.koiID, entity.koiID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(auctionID, koiID);
    }

}