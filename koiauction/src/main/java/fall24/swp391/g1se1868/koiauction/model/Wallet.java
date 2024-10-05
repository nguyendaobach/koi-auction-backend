package fall24.swp391.g1se1868.koiauction.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WalletID", nullable = false)
    private Integer id;

    @Column(name = "UserID", nullable = false)
    private Integer userID;

    @Column(name = "Amount", precision = 10, scale = 2)
    private Long amount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

}