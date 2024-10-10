package fall24.swp391.g1se1868.koiauction.model.koifishdto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class KoiFishDetailDTO {
    private Integer koiId;
    private String creatorFullName;
    private Integer koiTypeId;
    private String koiTypeName;
    private BigDecimal weight;
    private String sex;
    private LocalDate birthday;
    private String description;
    private BigDecimal length;
    private String status;
    private List<KoiFishMediaDTO> mediaList;  // Cập nhật thành danh sách chứa mediaType và url

    public KoiFishDetailDTO(Integer koiId, String creatorFullName, Integer koiTypeId, String koiTypeName,
                            BigDecimal weight, String sex, LocalDate birthday, String description,
                            BigDecimal length, String status, List<KoiFishMediaDTO> mediaList) {
        this.koiId = koiId;
        this.creatorFullName = creatorFullName;
        this.koiTypeId = koiTypeId;
        this.koiTypeName = koiTypeName;
        this.weight = weight;
        this.sex = sex;
        this.birthday = birthday;
        this.description = description;
        this.length = length;
        this.status = status;
        this.mediaList = mediaList;  // Cập nhật thành mediaList
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

    public Integer getKoiTypeId() {
        return koiTypeId;
    }

    public void setKoiTypeId(Integer koiTypeId) {
        this.koiTypeId = koiTypeId;
    }

    public String getKoiTypeName() {
        return koiTypeName;
    }

    public void setKoiTypeName(String koiTypeName) {
        this.koiTypeName = koiTypeName;
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

    public List<KoiFishMediaDTO> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<KoiFishMediaDTO> mediaList) {
        this.mediaList = mediaList;
    }

    // Getters and Setters
    // ...
}

