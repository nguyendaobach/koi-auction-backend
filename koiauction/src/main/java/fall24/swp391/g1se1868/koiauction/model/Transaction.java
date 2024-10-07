package fall24.swp391.g1se1868.koiauction.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "\"Transaction\"")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TransactionID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WalletID")
    private Wallet walletID;

    @Column(name = "\"Time\"")
    private Instant time;

    @Column(name = "Amount")
    private Long amount;

    @Column(name = "AuctionID", nullable = true)
    private Integer auctionID;

    @Nationalized
    @Column(name = "Status", length = 50)
    private String status;

    @Column(name="TransactionType")
    private String transactionType;

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Wallet getWalletID() {
        return walletID;
    }

    public void setWalletID(Wallet walletID) {
        this.walletID = walletID;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Integer getAuctionID() {
        return auctionID;
    }

    public void setAuctionID(Integer auctionID) {
        this.auctionID = auctionID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}