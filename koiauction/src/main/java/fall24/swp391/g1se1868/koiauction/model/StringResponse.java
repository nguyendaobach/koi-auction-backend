package fall24.swp391.g1se1868.koiauction.model;

public class StringResponse extends Throwable {
    private String message;
    public StringResponse(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {}
}
