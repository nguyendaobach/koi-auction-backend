package fall24.swp391.g1se1868.koiauction.model.koifishdto;

public class KoiMediaDTO {
    private String mediaType;
    private String url;

    public KoiMediaDTO(String mediaType, String url) {
        this.mediaType = mediaType;
        this.url = url;
    }

    // Getters and Setters
    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
