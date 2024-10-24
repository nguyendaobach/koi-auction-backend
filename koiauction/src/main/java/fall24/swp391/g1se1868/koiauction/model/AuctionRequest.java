package fall24.swp391.g1se1868.koiauction.model;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

public class AuctionRequest {

    
    private String auctionMethod; // Auction type: Ascending, Descending, Fixed-price, First-come

    
    private Instant startTime; // Start time for the auction

    
    private Instant endTime; // End time for the auction

    
    private Long startingPrice; // Starting price of the auction

    
    private Long buyoutPrice; // Buyout price of the auction

    
    private Long bidStep; // Minimum bid step

    private Long bidderDeposit;


    private List<Integer> koiIds; // List of Koi Fish IDs to be included in the auction

    // Getters and Setters


    public Long getBidderDeposit() {
        return bidderDeposit;
    }

    public void setBidderDeposit(Long bidderDeposit) {
        this.bidderDeposit = bidderDeposit;
    }

    public String getAuctionMethod() {
        return auctionMethod;
    }

    public void setAuctionMethod(String auctionMethod) {
        this.auctionMethod = auctionMethod;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Long getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(Long startingPrice) {
        this.startingPrice = startingPrice;
    }

    public Long getBuyoutPrice() {
        return buyoutPrice;
    }

    public void setBuyoutPrice(Long buyoutPrice) {
        this.buyoutPrice = buyoutPrice;
    }

    public Long getBidStep() {
        return bidStep;
    }

    public void setBidStep(Long bidStep) {
        this.bidStep = bidStep;
    }


    public List<Integer> getKoiIds() {
        return koiIds;
    }

    public void setKoiIds(List<Integer> koiIds) {
        this.koiIds = koiIds;
    }
}
