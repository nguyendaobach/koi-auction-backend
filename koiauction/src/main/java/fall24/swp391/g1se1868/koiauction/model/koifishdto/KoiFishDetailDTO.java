package fall24.swp391.g1se1868.koiauction.model.koifishdto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class KoiFishDetailDTO { //koi fish controller
    private Integer koiId;
    private String koiName;
    private String creatorFullName;
    private String koiTypeName;
    private String koiOriginName;
    private BigDecimal weight;
    private String sex;
    private LocalDate birthday;
    private String description;
    private BigDecimal length;
    private String status;
    private List<KoiMediaDTO> mediaList;  // Cập nhật thành danh sách chứa mediaType và url

    public KoiFishDetailDTO(Integer koiId, String koiName, String creatorFullName, String koiTypeName, String koiOriginName, BigDecimal weight, String sex, LocalDate birthday, String description, BigDecimal length, String status, List<KoiMediaDTO> mediaList) {
        this.koiId = koiId;
        this.koiName = koiName;
        this.creatorFullName = creatorFullName;
        this.koiTypeName = koiTypeName;
        this.koiOriginName = koiOriginName;
        this.weight = weight;
        this.sex = sex;
        this.birthday = birthday;
        this.description = description;
        this.length = length;
        this.status = status;
        this.mediaList = mediaList;
    }

    public Integer getKoiId() {
        return koiId;
    }

    public void setKoiId(Integer koiId) {
        this.koiId = koiId;
    }

    public String getCreatorFullName() {
        return creatorFullName;
    }

    public void setCreatorFullName(String creatorFullName) {
        this.creatorFullName = creatorFullName;
    }


    public String getKoiTypeName() {
        return koiTypeName;
    }

    public void setKoiTypeName(String koiTypeName) {
        this.koiTypeName = koiTypeName;
    }

    public String getKoiOriginName() {
        return koiOriginName;
    }

    public void setKoiOriginName(String koiOriginName) {
        this.koiOriginName = koiOriginName;
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

    public String getKoiName() {
        return koiName;
    }

    public void setKoiName(String koiName) {
        this.koiName = koiName;
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

    public List<KoiMediaDTO> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<KoiMediaDTO> mediaList) {
        this.mediaList = mediaList;
    }

    // Getters and Setters
    // ...
}

