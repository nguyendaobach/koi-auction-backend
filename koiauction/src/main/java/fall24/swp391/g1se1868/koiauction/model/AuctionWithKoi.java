package fall24.swp391.g1se1868.koiauction.model;
import java.util.List;


public class AuctionWithKoi {
        private Auction auction;
        private List<KoiFish> koiFish;

        public AuctionWithKoi(Auction auction, List<KoiFish> koiFish) {
            this.auction = auction;
            this.koiFish = koiFish;
        }

        public Auction getAuction() {
            return auction;
        }

        public void setAuction(Auction auction) {
            this.auction = auction;
        }

        public List<KoiFish> getKoiFish() {
            return koiFish;
        }

        public void setKoiFish(List<KoiFish> koiFish) {
            this.koiFish = koiFish;
        }
}


