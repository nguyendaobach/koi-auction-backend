package fall24.swp391.g1se1868.koiauction.model;

public class UserChat {
    private Integer userId;
    private String fullName;


    public UserChat(Integer userId, String fullName) {
        this.userId = userId;
        this.fullName = fullName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
