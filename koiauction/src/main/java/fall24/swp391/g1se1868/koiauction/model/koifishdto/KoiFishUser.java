package fall24.swp391.g1se1868.koiauction.model.koifishdto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class KoiFishUser {
    private Integer id;
    private String name;
    private String userFullName;  // Chỉ chứa fullName của user
    private String countryID;
    private String koiTypeID;
    private BigDecimal weight;
    private String sex;
    private LocalDate birthday;
    private String description;
    private BigDecimal length;
    private String status;
    private String headerImageUrl;  // URL của hình ảnh header

    public KoiFishUser(Integer id, String name, String userFullName, String countryID, String koiTypeID, BigDecimal weight, String sex, LocalDate birthday, String description, BigDecimal length, String status, String headerImageUrl) {
        this.id = id;
        this.name = name;
        this.userFullName = userFullName;
        this.countryID = countryID;
        this.koiTypeID = koiTypeID;
        this.weight = weight;
        this.sex = sex;
        this.birthday = birthday;
        this.description = description;
        this.length = length;
        this.status = status;
        this.headerImageUrl = headerImageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getCountryID() {
        return countryID;
    }

    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }

    public String getKoiTypeID() {
        return koiTypeID;
    }

    public void setKoiTypeID(String koiTypeID) {
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

    public String getHeaderImageUrl() {
        return headerImageUrl;
    }

    public void setHeaderImageUrl(String headerImageUrl) {
        this.headerImageUrl = headerImageUrl;
    }
}