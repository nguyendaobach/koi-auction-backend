package fall24.swp391.g1se1868.koiauction.model.auction;

public class KoiAuctionResponseDTO {
    private Integer auctionId;  // Thêm auctionId
    private String koiName;
    private String variety;
    private String startingBid;
    private String estimatedValue;
    private String breederName;
    private String sex;
    private String bornIn;
    private String size;
    private String timeLeft;
    private String imageUrl; // Hình ảnh Header Image
    private String action; // Đăng nhập/Đăng ký để đấu giá

    public KoiAuctionResponseDTO(Integer auctionId,String koiName, String variety, String startingBid, String estimatedValue, String breederName, String sex, String bornIn, String size, String timeLeft, String imageUrl, String action) {
        this.auctionId=auctionId;
        this.koiName = koiName;
        this.variety = variety;
        this.startingBid = startingBid;
        this.estimatedValue = estimatedValue;
        this.breederName = breederName;
        this.sex = sex;
        this.bornIn = bornIn;
        this.size = size;
        this.timeLeft = timeLeft;
        this.imageUrl = imageUrl;
        this.action = action;
    }

    public KoiAuctionResponseDTO() {

    }

    public Integer getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Integer auctionId) {
        this.auctionId = auctionId;
    }

    public String getKoiName() {
        return koiName;
    }

    public void setKoiName(String koiName) {
        this.koiName = koiName;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public String getStartingBid() {
        return startingBid;
    }

    public void setStartingBid(String startingBid) {
        this.startingBid = startingBid;
    }

    public String getEstimatedValue() {
        return estimatedValue;
    }

    public void setEstimatedValue(String estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    public String getBreederName() {
        return breederName;
    }

    public void setBreederName(String breederName) {
        this.breederName = breederName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBornIn() {
        return bornIn;
    }

    public void setBornIn(String bornIn) {
        this.bornIn = bornIn;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(String timeLeft) {
        this.timeLeft = timeLeft;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
