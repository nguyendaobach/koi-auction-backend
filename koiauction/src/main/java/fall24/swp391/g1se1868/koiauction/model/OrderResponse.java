package fall24.swp391.g1se1868.koiauction.model;

import java.time.Instant;

public class OrderResponse {
    private Integer orderId;
    private Integer bidderId;
    private Integer auctionId;
    private String address;
    private Instant date;
    private Long price;
    private String phoneNumber;
    private String note;
    private String status;

    public OrderResponse(Integer orderId, Integer bidderId, Integer auctionId, String address,
                         Instant date, Long price, String phoneNumber, String note, String status) {
        this.orderId = orderId;
        this.bidderId = bidderId;
        this.auctionId = auctionId;
        this.address = address;
        this.date = date;
        this.price = price;
        this.phoneNumber = phoneNumber;
        this.note = note;
        this.status = status;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Integer auctionId) {
        this.auctionId = auctionId;
    }

    public Integer getBidderId() {
        return bidderId;
    }

    public void setBidderId(Integer bidderId) {
        this.bidderId = bidderId;
    }
}
