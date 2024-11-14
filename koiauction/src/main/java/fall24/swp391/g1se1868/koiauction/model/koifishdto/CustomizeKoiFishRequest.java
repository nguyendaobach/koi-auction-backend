package fall24.swp391.g1se1868.koiauction.model.koifishdto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

public class CustomizeKoiFishRequest {
    private String imageHeader;
    private List<String> imageDetail;
    private String video;
    private String name;
    private BigDecimal weight;
    private String sex;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    private String description;
    private BigDecimal length;
    private Integer countryID;
    private Integer koiTypeID;

    public String getImageHeader() {
        return imageHeader;
    }

    public void setImageHeader(String imageHeader) {
        this.imageHeader = imageHeader;
    }

    public List<String> getImageDetail() {
        return imageDetail;
    }

    public void setImageDetail(List<String> imageDetail) {
        this.imageDetail = imageDetail;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
