package fall24.swp391.g1se1868.koiauction.model;

import fall24.swp391.g1se1868.koiauction.service.ChangePassword;

public class VerifyAndChangePasswordRequest {
    private String otp;
    private String email;
    private ChangePassword changePassword;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ChangePassword getChangePassword() {
        return changePassword;
    }

    public void setChangePassword(ChangePassword changePassword) {
        this.changePassword = changePassword;
    }
}
