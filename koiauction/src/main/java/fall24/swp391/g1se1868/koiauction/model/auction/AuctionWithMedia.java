package fall24.swp391.g1se1868.koiauction.model.auction;

import fall24.swp391.g1se1868.koiauction.model.Auction;
import fall24.swp391.g1se1868.koiauction.model.koifishdto.KoiFishWithMediaAll;

import java.util.List;

public class AuctionWithMedia {
    private Auction auction;
    private List<KoiFishWithMediaAll> koiFishWithMediaAlls;

    public AuctionWithMedia(Auction auction, List<KoiFishWithMediaAll> koiFishWithMediaAlls) {
        this.auction = auction;
        this.koiFishWithMediaAlls = koiFishWithMediaAlls;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public List<KoiFishWithMediaAll> getKoiFishWithMediaAlls() {
        return koiFishWithMediaAlls;
    }

    public void setKoiFishWithMediaAlls(List<KoiFishWithMediaAll> koiFishWithMediaAlls) {
        this.koiFishWithMediaAlls = koiFishWithMediaAlls;
    }
}
