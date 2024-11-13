package fall24.swp391.g1se1868.koiauction.model.koifishdto;

public class KoiActiveResponse {
    private int id;
    private String nameFish;
    private String imgHeader;

    public KoiActiveResponse(int id, String nameFish, String imgHeader) {
        this.id = id;
        this.nameFish = nameFish;
        this.imgHeader = imgHeader;
    }

    public KoiActiveResponse() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameFish() {
        return nameFish;
    }

    public void setNameFish(String nameFish) {
        this.nameFish = nameFish;
    }

    public String getImgHeader() {
        return imgHeader;
    }

    public void setImgHeader(String imgHeader) {
        this.imgHeader = imgHeader;
    }
}
