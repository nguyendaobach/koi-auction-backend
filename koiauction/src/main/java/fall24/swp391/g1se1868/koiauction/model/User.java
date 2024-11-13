package fall24.swp391.g1se1868.koiauction.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Entity
@Table(name = "\"User\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "Role", length = 50)
    private String role;

    @Nationalized
    @Column(name = "UserName", length = 100)
    private String userName;

    @Nationalized
    @Column(name = "FullName", length = 200)
    private String fullName;

    @Nationalized
    @Column(name = "PhoneNumber", length = 20)
    private String phoneNumber;

    @Nationalized
    @Column(name = "Email", length = 100)
    private String email;

    @Nationalized
    @Column(name = "Password", length = 100)
    private String password;

    @Nationalized
    @Lob
    @Column(name = "Address")
    private String address;

    @Column(name = "CreateAt")
    private Instant createAt;

    @Column(name = "UpdateAt")
    private Instant updateAt;

    @Nationalized
    @Column(name = "Status", length = 50)
    private String status;
    public User() {

    }

    public User(String email) {
        this.email = email;
    }

    public User(Integer id) {
        this.id = id;
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public User(Integer id, String role, String userName, String phoneNumber, String fullName, String password, String email, String address, Instant updateAt, String status, Instant createAt) {
        this.id = id;
        this.role = role;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.password = password;
        this.email = email;
        this.address = address;
        this.updateAt = updateAt;
        this.status = status;
        this.createAt = createAt;
    }

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private ForgotPassword forgotPassword;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Instant getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Instant createAt) {
        this.createAt = createAt;
    }

    public Instant getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Instant updateAt) {
        this.updateAt = updateAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}