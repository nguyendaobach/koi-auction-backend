package fall24.swp391.g1se1868.koiauction.model.auction;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public class KoiFishAuctionAll {
    private Integer auctionId;
    private Long startPrice;
    private String status;
    private Long finalPrice;
    private Instant startTime;
    private Instant endTime;
    private String method;
    private List<KoiInfo> koiInfoList;

    public KoiFishAuctionAll() {
    }

    public Integer getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Integer auctionId) {
        this.auctionId = auctionId;
    }

    public Long getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(Long startPrice) {
        this.startPrice = startPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(Long finalPrice) {
        this.finalPrice = finalPrice;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public List<KoiInfo> getKoiInfoList() {
        return koiInfoList;
    }

    public void setKoiInfoList(List<KoiInfo> koiInfoList) {
        this.koiInfoList = koiInfoList;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public KoiFishAuctionAll(Integer auctionId, Long startPrice, String status, Long finalPrice, Instant startTime, Instant endTime, String method, List<KoiInfo> koiInfoList) {
        this.auctionId = auctionId;
        this.startPrice = startPrice;
        this.status = status;
        this.finalPrice = finalPrice;
        this.startTime = startTime;
        this.endTime = endTime;
        this.method = method;
        this.koiInfoList = koiInfoList;
    }


}

