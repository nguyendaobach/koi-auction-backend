package fall24.swp391.g1se1868.koiauction.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AuctionID", nullable = false)
    private Integer id;

    @Column(name = "BreederID")
    private Integer breederID;

    @Column(name = "StaffID")
    private Integer staffID;

    @Column(name = "WinnerID")
    private Integer winnerID;

    @Nationalized
    @Column(name = "AuctionMethod", length = 50)
    private String auctionMethod;

    @Column(name = "StartTime")
    private Instant startTime;

    @Column(name = "EndTime")
    private Instant endTime;

    @Column(name = "BreederDeposit", precision = 10, scale = 2)
    private BigDecimal breederDeposit;

    @Column(name = "BidderDeposit", precision = 10, scale = 2)
    private BigDecimal bidderDeposit;

    @Column(name = "StartingPrice", precision = 10, scale = 2)
    private BigDecimal startingPrice;

    @Column(name = "BuyoutPrice", precision = 10, scale = 2)
    private BigDecimal buyoutPrice;

    @Column(name = "FinalPrice", precision = 10, scale = 2)
    private BigDecimal finalPrice;

    @Column(name = "BidStep", precision = 10, scale = 2)
    private BigDecimal bidStep;

    @Column(name = "AuctionFee", precision = 10, scale = 2)
    private BigDecimal auctionFee;

    @Column(name = "CreateAt")
    private Instant createAt;

    @Nationalized
    @Column(name = "Status", length = 50)
    private String status;

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

    public BigDecimal getBreederDeposit() {
        return breederDeposit;
    }

    public void setBreederDeposit(BigDecimal breederDeposit) {
        this.breederDeposit = breederDeposit;
    }

    public BigDecimal getBidderDeposit() {
        return bidderDeposit;
    }

    public void setBidderDeposit(BigDecimal bidderDeposit) {
        this.bidderDeposit = bidderDeposit;
    }

    public BigDecimal getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(BigDecimal startingPrice) {
        this.startingPrice = startingPrice;
    }

    public BigDecimal getBuyoutPrice() {
        return buyoutPrice;
    }

    public void setBuyoutPrice(BigDecimal buyoutPrice) {
        this.buyoutPrice = buyoutPrice;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public BigDecimal getBidStep() {
        return bidStep;
    }

    public void setBidStep(BigDecimal bidStep) {
        this.bidStep = bidStep;
    }

    public BigDecimal getAuctionFee() {
        return auctionFee;
    }

    public void setAuctionFee(BigDecimal auctionFee) {
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

}