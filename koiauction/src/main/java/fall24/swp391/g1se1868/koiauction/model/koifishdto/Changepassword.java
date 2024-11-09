package fall24.swp391.g1se1868.koiauction.model.koifishdto;

public class Changepassword {
    private String password;
    private String confirm;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public Changepassword(String password, String confirm) {
        this.password = password;
        this.confirm = confirm;
    }

    public Changepassword() {
    }
}
