package fall24.swp391.g1se1868.koiauction.model.auction;

import fall24.swp391.g1se1868.koiauction.model.koifishdto.KoiDataDTO;

import java.time.Instant;
import java.util.List;

public class AuctionDetailDTO {
    private Integer id;
    private Integer breederID;
    private String breederFullName;
    private Integer staffID;
    private Integer winnerID;
    private String auctionMethod;
    private Instant startTime; // Sử dụng Instant
    private Instant endTime;   // Sử dụng Instant
    private Long breederDeposit; // Sử dụng Long
    private Long bidderDeposit;  // Sử dụng Long
    private Long startingPrice;  // Sử dụng Long
    private Long buyoutPrice;    // Sử dụng Long
    private Long finalPrice;     // Sử dụng Long
    private Long bidStep;        // Sử dụng Long
    private Long auctionFee;     // Sử dụng Long
    private Instant createAt;    // Sử dụng Instant
    private String status;
    private List<KoiDataDTO> koiData;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBreederID() {
        return breederID;
    }

    public void setBreederID(Integer breederID) {
        this.breederID = breederID;
    }

    public String getBreederFullName() {
        return breederFullName;
    }

    public void setBreederFullName(String breederFullName) {
        this.breederFullName = breederFullName;
    }

    public Integer getStaffID() {
        return staffID;
    }

    public void setStaffID(Integer staffID) {
        this.staffID = staffID;
    }

    public Integer getWinnerID() {
        return winnerID;
    }

    public void setWinnerID(Integer winnerID) {
        this.winnerID = winnerID;
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

    public Long getBreederDeposit() {
        return breederDeposit;
    }

    public void setBreederDeposit(Long breederDeposit) {
        this.breederDeposit = breederDeposit;
    }

    public Long getBidderDeposit() {
        return bidderDeposit;
    }

    public void setBidderDeposit(Long bidderDeposit) {
        this.bidderDeposit = bidderDeposit;
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

    public Long getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(Long finalPrice) {
        this.finalPrice = finalPrice;
    }

    public Long getBidStep() {
        return bidStep;
    }

    public void setBidStep(Long bidStep) {
        this.bidStep = bidStep;
    }

    public Long getAuctionFee() {
        return auctionFee;
    }

    public void setAuctionFee(Long auctionFee) {
        this.auctionFee = auctionFee;
    }

    public Instant getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Instant createAt) {
        this.createAt = createAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<KoiDataDTO> getKoiData() {
        return koiData;
    }

    public void setKoiData(List<KoiDataDTO> koiData) {
        this.koiData = koiData;
    }
}
