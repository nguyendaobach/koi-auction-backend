package fall24.swp391.g1se1868.koiauction.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Entity
public class AuctionParticipant {
    @EmbeddedId
    private AuctionParticipantId id;

    @MapsId("auctionID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AuctionID", nullable = false)
    private Auction auctionID;

    @MapsId("userID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserID", nullable = false)
    private User userID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TransactionID")
    private Transaction transactionID;

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

    public Auction getAuctionID() {
        return auctionID;
    }

    public void setAuctionID(Auction auctionID) {
        this.auctionID = auctionID;
    }

    public User getUserID() {
        return userID;
    }

    public void setUserID(User userID) {
        this.userID = userID;
    }

    public Transaction getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(Transaction transactionID) {
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