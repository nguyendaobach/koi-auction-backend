package fall24.swp391.g1se1868.koiauction.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

@Entity
public class KoiMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ImageID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KoiID")
    private KoiFish koiID;

    @Column(name = "Url")
    private String url;

    @Column(name = "MediaType", length = 50)
    private String mediaType;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public KoiFish getKoiID() {
        return koiID;
    }

    public void setKoiID(KoiFish koiID) {
        this.koiID = koiID;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

}