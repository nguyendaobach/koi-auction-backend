package fall24.swp391.g1se1868.koiauction.model.auction;

import fall24.swp391.g1se1868.koiauction.model.Auction;

import java.util.List;

public class KoiAuctionResponseDTO {
    private Auction auction;
    private List<Integer> koiFish;

    public KoiAuctionResponseDTO(Auction auction, List<Integer> koiFish) {
        this.auction = auction;
        this.koiFish = koiFish;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public List<Integer> getKoiFish() {
        return koiFish;
    }

    public void setKoiFish(List<Integer> koiFish) {
        this.koiFish = koiFish;
    }
}



