package fall24.swp391.g1se1868.koiauction.model;

public class OrderRequest {
    private int auctionID;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String note;

    public OrderRequest() {
    }

    public OrderRequest(int auctionID, String fullName, String phoneNumber, String address, String note) {
        this.auctionID = auctionID;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.note = note;
    }

    public int getAuctionID() {
        return auctionID;
    }

    public void setAuctionID(int auctionID) {
        this.auctionID = auctionID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
