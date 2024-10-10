package fall24.swp391.g1se1868.koiauction.model;

public class UserKoifish {
    private String userName;
    private String fullName;
    private String email;
    private String address;

    public UserKoifish(String userName, String fullName, String email, String address) {
        this.userName = userName;
        this.fullName = fullName;
        this.email = email;
        this.address = address;
    }

    // Getters and Setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
