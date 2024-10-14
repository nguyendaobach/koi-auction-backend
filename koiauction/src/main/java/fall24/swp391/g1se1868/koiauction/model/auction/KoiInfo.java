package fall24.swp391.g1se1868.koiauction.model.auction;

public class KoiInfo {
    private Integer id;
    private String koiName;
    private String headerImageUrl;

    public KoiInfo(Integer id, String koiName, String headerImageUrl) {
        this.id = id;
        this.koiName = koiName;
        this.headerImageUrl = headerImageUrl;
    }

    public KoiInfo() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKoiName() {
        return koiName;
    }

    public void setKoiName(String koiName) {
        this.koiName = koiName;
    }

    public String getHeaderImageUrl() {
        return headerImageUrl;
    }

    public void setHeaderImageUrl(String headerImageUrl) {
        this.headerImageUrl = headerImageUrl;
    }
}
