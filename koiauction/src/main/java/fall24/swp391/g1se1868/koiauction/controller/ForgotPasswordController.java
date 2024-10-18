package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.ForgotPassword;
import fall24.swp391.g1se1868.koiauction.model.MailBody;
import fall24.swp391.g1se1868.koiauction.model.StringResponse;
import fall24.swp391.g1se1868.koiauction.model.User;
import fall24.swp391.g1se1868.koiauction.repository.ForgotpasswordRepository;
import fall24.swp391.g1se1868.koiauction.repository.UserRepository;
import fall24.swp391.g1se1868.koiauction.service.ChangePassword;
import fall24.swp391.g1se1868.koiauction.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/forgot-password")
public class ForgotPasswordController {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new StringResponse("Invalid email address."));
        }

        forgotpasswordRepository.findUser(user).ifPresent(fp -> {
            forgotpasswordRepository.deleteByFpid(fp.getFpid());
            System.out.println("Deleted existing ForgotPassword record: " + fp.getFpid());
        });

        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This is the OTP for your Forgot Password request: " + otp)
                .subject("OTP for Forgot Password")
                .build();

        ForgotPassword forgotPassword = ForgotPassword.builder()
                .otp(otp)
                .expirationDate(Date.from(Instant.now().plusSeconds(70))) // 70 seconds expiration
                .user(user)
                .build();

        emailService.sendSimpleMessage(mailBody);
        forgotpasswordRepository.save(forgotPassword);

        return ResponseEntity.ok(new StringResponse("Email sent successfully."));
    }

    @PostMapping("/verifyAndChangePassword")
    public ResponseEntity<StringResponse> verifyAndChangePassword(
            @RequestBody Integer otp,
            @RequestBody String email,
            @RequestBody ChangePassword changePassword) {

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new StringResponse("Invalid email address."));
        }

        Optional<ForgotPassword> optionalForgotPassword = forgotpasswordRepository.findByOtpAndUser(otp, user);
        if (optionalForgotPassword.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new StringResponse("Invalid OTP for email: " + email));
        }

        ForgotPassword fp = optionalForgotPassword.get();

        if (fp.getExpirationDate().before(Date.from(Instant.now()))) {
            forgotpasswordRepository.deleteById(fp.getFpid()); // Xóa bản ghi nếu OTP hết hạn
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new StringResponse("OTP has expired."));
        }

        if (!Objects.equals(changePassword.password(), changePassword.repeatPassword())) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new StringResponse("Passwords do not match. Please try again."));
        }

        String encodedPassword = encoder.encode(changePassword.password());
        userRepository.updatePassword(email, encodedPassword);

        forgotpasswordRepository.delete(fp);

        return ResponseEntity.ok(new StringResponse("Password has been changed successfully."));
    }

    private Integer otpGenerator() {
        Random random = new Random();
        return random.nextInt(100_000, 999_999); // Sinh OTP từ 100000 đến 999999
    }
}
