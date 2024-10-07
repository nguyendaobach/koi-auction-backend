package fall24.swp391.g1se1868.koiauction.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Entity
public class AuctionParticipant {
    @EmbeddedId
    private AuctionParticipantId id;

    @MapsId("auctionID")
    @Column(name = "AuctionID")
    private Integer auctionID;

    @MapsId("userID")
    @Column(name = "UserID", nullable = false)
    private Integer userID;


    @Column(name = "TransactionID")
    private Integer transactionID;

    @Column(name = "ParticipantAuctionDate")
    private Instant participantAuctionDate;

    @Nationalized
    @Column(name = "Status", length = 50)
    private String status;

    public AuctionParticipantId getId() {
        return id;
    }

    public void setId(AuctionParticipantId id) {
        this.id = id;
    }

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

    public Integer getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(Integer transactionID) {
        this.transactionID = transactionID;
    }

    public Instant getParticipantAuctionDate() {
        return participantAuctionDate;
    }

    public void setParticipantAuctionDate(Instant participantAuctionDate) {
        this.participantAuctionDate = participantAuctionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}