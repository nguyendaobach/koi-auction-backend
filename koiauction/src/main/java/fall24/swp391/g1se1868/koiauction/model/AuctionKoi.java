package fall24.swp391.g1se1868.koiauction.model;

import jakarta.persistence.*;

@Entity
public class AuctionKoi {
    @EmbeddedId
    private AuctionKoiId id;

    @MapsId("auctionID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AuctionID", nullable = false)
    private Auction auctionID;

    @MapsId("koiID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "KoiID", nullable = false)
    private KoiFish koiID;

    public AuctionKoiId getId() {
        return id;
    }

    public void setId(AuctionKoiId id) {
        this.id = id;
    }

    public Auction getAuctionID() {
        return auctionID;
    }

    public void setAuctionID(Auction auctionID) {
        this.auctionID = auctionID;
    }

    public KoiFish getKoiID() {
        return koiID;
    }

    public void setKoiID(KoiFish koiID) {
        this.koiID = koiID;
    }

}