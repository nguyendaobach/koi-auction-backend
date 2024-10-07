package fall24.swp391.g1se1868.koiauction.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

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

    @Column(name = "BreederDeposit" )
    private Long breederDeposit;

    @Column(name = "BidderDeposit" )
    private Long bidderDeposit;

    @Column(name = "StartingPrice" )
    private Long startingPrice;

    @Column(name = "BuyoutPrice" )
    private Long buyoutPrice;

    @Column(name = "FinalPrice" )
    private Long finalPrice;

    @Column(name = "BidStep" )
    private Long bidStep;

    @Column(name = "AuctionFee" )
    private Long auctionFee;

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

}