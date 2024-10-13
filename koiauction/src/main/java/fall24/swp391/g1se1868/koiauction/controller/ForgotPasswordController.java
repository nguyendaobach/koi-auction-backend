package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.ForgotPassword;
import fall24.swp391.g1se1868.koiauction.model.MailBody;
import fall24.swp391.g1se1868.koiauction.model.StringResponse;
import fall24.swp391.g1se1868.koiauction.model.User;
import fall24.swp391.g1se1868.koiauction.repository.ForgotpasswordRepository;
import fall24.swp391.g1se1868.koiauction.repository.UserRepository;
import fall24.swp391.g1se1868.koiauction.service.ChangePassword;
import fall24.swp391.g1se1868.koiauction.service.EmailService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/forgot-password")
public class ForgotPasswordController {

    private BCryptPasswordEncoder encoder =new BCryptPasswordEncoder(12);

    private final UserRepository userRepository;

    private final EmailService emailService;

    private final ForgotpasswordRepository forgotpasswordRepository;

    @Autowired
    public ForgotPasswordController(UserRepository userRepository, EmailService emailService, ForgotpasswordRepository forgotpasswordRepository) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.forgotpasswordRepository = forgotpasswordRepository;
    }

    @Transactional
    @PostMapping("verifyMail/{email}")
    public ResponseEntity<StringResponse> verifyMail(@PathVariable String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new StringResponse("Please enter invalid email"));
        }

        // Tìm và xóa bản ghi ForgotPassword cũ (nếu có)
        Optional<ForgotPassword> existingForgotPassword = forgotpasswordRepository.findUser(user);
        if (existingForgotPassword.isPresent()) {
            ForgotPassword fpToDelete = existingForgotPassword.get();
            forgotpasswordRepository.deleteByFpid(fpToDelete.getFpid());
            System.out.println("Deleted existing ForgotPassword record: " + fpToDelete.getFpid());
        }
        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This is the OTP for your Forgot Password request " + otp)
                .subject("OTP for Forgot Password")
                .build();
        // Tạo và lưu mới một bản ghi ForgotPassword
        ForgotPassword forgotPassword = ForgotPassword.builder()
                .otp(otp)
                .expirationDate(new Date(System.currentTimeMillis() + 70 * 1000))
                .user(user)
                .build();
        emailService.sendSimpleMessage(mailBody);
        forgotpasswordRepository.save(forgotPassword);

        return ResponseEntity.ok(new StringResponse("Email sent successfully"));
    }



    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<StringResponse> verifyOtp(@PathVariable Integer otp, @PathVariable String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new StringResponse("Invalid email."));
        }

        // Tìm OTP và kiểm tra
        ForgotPassword fp = forgotpasswordRepository.findByOtpAndUser(otp, user)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid OTP for email: " + email));

        // Kiểm tra OTP hết hạn
        if (fp.getExpirationDate().before(Date.from(Instant.now()))) {
            forgotpasswordRepository.deleteById(fp.getFpid());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new StringResponse("OTP has expired."));
        }

        // Nếu OTP hợp lệ, tạo mật khẩu mới
        String newPassword = generateRandomPassword();
        String encodedPassword = encoder.encode(newPassword);

        // Cập nhật mật khẩu mới trong cơ sở dữ liệu
        userRepository.updatePassword(email, encodedPassword);

        // Gửi mật khẩu mới qua email
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("Your password has been reset. Your new password is: " + newPassword)
                .subject("New Password")
                .build();
        emailService.sendSimpleMessage(mailBody);

        // Xóa bản ghi ForgotPassword sau khi hoàn tất
        forgotpasswordRepository.deleteById(fp.getFpid());

        return ResponseEntity.ok(new StringResponse("OTP verified! New password has been sent to your email."));
    }




//    @PostMapping("/changePassword/{email}")
//    public ResponseEntity<StringResponse> changePasswordHandler(@RequestBody ChangePassword changePassword, @PathVariable String email){
//        if(!Objects.equals(changePassword.password(),changePassword.repeatPassword())){
//            return new ResponseEntity<>(new StringResponse("Please enter the password again"), HttpStatus.EXPECTATION_FAILED);
//        }
//        String encodePasword = encoder.encode(changePassword.password());
//        userRepository.updatePassword(email,encodePasword);
//        return ResponseEntity.ok(new StringResponse("Password has been changed"));
//    }
//
//
    private Integer otpGenerator(){
        Random random=new Random();
        return random.nextInt(100_000,999_999);
    }
    private String generateRandomPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 10; i++) {  // Mật khẩu dài 10 ký tự
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        return password.toString();
    }

}
