package fall24.swp391.g1se1868.koiauction.model.koifishdto;

import fall24.swp391.g1se1868.koiauction.model.KoiFish;
import fall24.swp391.g1se1868.koiauction.model.KoiMedia;

import java.util.List;

public class KoiFishWithMediaAll {
    private KoiFish koiFish;
    private List<KoiMedia> koiMediaList;

    public KoiFishWithMediaAll(KoiFish koiFish, List<KoiMedia> koiMediaList) {
        this.koiFish = koiFish;
        this.koiMediaList = koiMediaList;
    }

    public KoiFish getKoiFish() {
        return koiFish;
    }

    public void setKoiFish(KoiFish koiFish) {
        this.koiFish = koiFish;
    }

    public List<KoiMedia> getKoiMediaList() {
        return koiMediaList;
    }

    public void setKoiMediaList(List<KoiMedia> koiMediaList) {
        this.koiMediaList = koiMediaList;
    }
}
