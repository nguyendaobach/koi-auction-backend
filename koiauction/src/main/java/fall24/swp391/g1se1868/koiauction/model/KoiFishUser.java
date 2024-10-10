package fall24.swp391.g1se1868.koiauction.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class KoiFishUser {
    private Integer id;
    private UserKoifish userID; // Sử dụng UserDTO
    private Integer countryID;
    private Integer koiTypeID;
    private BigDecimal weight;
    private String sex;
    private LocalDate birthday;
    private String description;
    private BigDecimal length;
    private String status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserKoifish getUserID() {
        return userID;
    }

    public void setUserID(UserKoifish userID) {
        this.userID = userID;
    }

    public Integer getCountryID() {
        return countryID;
    }

    public void setCountryID(Integer countryID) {
        this.countryID = countryID;
    }

    public Integer getKoiTypeID() {
        return koiTypeID;
    }

    public void setKoiTypeID(Integer koiTypeID) {
        this.koiTypeID = koiTypeID;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public KoiFishUser(Integer id, UserKoifish userID, Integer countryID, Integer koiTypeID, BigDecimal weight, String sex, LocalDate birthday, String description, BigDecimal length, String status) {
        this.id = id;
        this.userID = userID;
        this.countryID = countryID;
        this.koiTypeID = koiTypeID;
        this.weight = weight;
        this.sex = sex;
        this.birthday = birthday;
        this.description = description;
        this.length = length;
        this.status = status;
    }
}
