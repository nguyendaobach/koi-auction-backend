package fall24.swp391.g1se1868.koiauction.model.koifishdto;

import fall24.swp391.g1se1868.koiauction.model.KoiFish;
import fall24.swp391.g1se1868.koiauction.model.KoiMedia;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class KoiDataDTO {
    private Integer id;
    private String country;
    private String koiType;
    private BigDecimal weight;
    private String sex;
    private LocalDate birthday;
    private String description;
    private BigDecimal length;
    private String status;
    private String koiName;
    private List<KoiMediaDTO> koiMedia;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getKoiType() {
        return koiType;
    }

    public void setKoiType(String koiType) {
        this.koiType = koiType;
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

    public BigDecimal getWeight() {
        return weight;
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

    public String getKoiName() {
        return koiName;
    }

    public void setKoiName(String koiName) {
        this.koiName = koiName;
    }

    public List<KoiMediaDTO> getKoiMedia() {
        return koiMedia;
    }

    public void setKoiMedia(List<KoiMediaDTO> koiMedia) {
        this.koiMedia = koiMedia;
    }
}
