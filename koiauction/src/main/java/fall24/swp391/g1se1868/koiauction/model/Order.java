package fall24.swp391.g1se1868.koiauction.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Entity
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BidderID")
    private User bidderID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AuctionID")
    private Auction auctionID;

    @Column(name = "FullName")
    private String fullName;

    @Nationalized
    @Lob
    @Column(name = "Address")
    private String address;

    @Column(name = "\"Date\"")
    private Instant date;

    @Column(name = "Price", precision = 10, scale = 2)
    private  Long price;

    @Nationalized
    @Column(name = "PhoneNumber", length = 20)
    private String phoneNumber;

    @Nationalized
    @Lob
    @Column(name = "Note")
    private String note;

    @Nationalized
    @Column(name = "Status", length = 50)
    private String status;


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getBidderID() {
        return bidderID;
    }

    public void setBidderID(User bidderID) {
        this.bidderID = bidderID;
    }

    public Auction getAuctionID() {
        return auctionID;
    }

    public void setAuctionID(Auction auctionID) {
        this.auctionID = auctionID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public  Long getPrice() {
        return price;
    }

    public void setPrice( Long price) {
        this.price = price;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}