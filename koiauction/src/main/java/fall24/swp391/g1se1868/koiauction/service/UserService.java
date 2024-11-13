package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.model.koifishdto.Changepassword;
import fall24.swp391.g1se1868.koiauction.repository.ForgotpasswordRepository;
import fall24.swp391.g1se1868.koiauction.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class UserService {

    @Value("${token.expire}")
    private int tokenExpire;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    JwtService jwtService;

    @Autowired
    EmailService emailService;

    @Autowired
    RegisterService registerService;

    @Autowired
    ForgotpasswordRepository forgotpasswordRepository;
    private BCryptPasswordEncoder encoder =new BCryptPasswordEncoder(12);

    public ResponseEntity<?> register(UserRegister userRegister) {
        try {
            if (!verifyEmail(userRegister.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is invalid or already in use.");
            }
            Integer otp = registerService.generateOTP();
            registerService.saveOTP(userRegister.getEmail(), otp);
            sendOTPEmail(userRegister.getEmail(), otp);

            return ResponseEntity.status(HttpStatus.CREATED).body("OTP sent successfully to your email.");
        } catch (Exception e) {
            return new ResponseEntity<>(new StringResponse("Registration failed: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public boolean verifyUserName(String username){
        try {
            User user = userRepository.findByUserName(username);
            return user==null&&username!=null&&!username.isEmpty()&&!username.isBlank()?true:false;
        } catch (IncorrectResultSizeDataAccessException e) {
            return false;
        }
    }
    public boolean verifyEmail(String email){
        try {
            User user = userRepository.findByEmail(email);
            return user==null&&email!=null&&!email.isEmpty()&&!email.isBlank()?true:false;
        } catch (IncorrectResultSizeDataAccessException e) {
            return false;
        }
    }
    public boolean verifyPhoneNumber(String phoneNumber){
        try {
            User user = userRepository.findByPhoneNumber(phoneNumber);
            return user==null&&phoneNumber!=null&&!phoneNumber.isEmpty()&&!phoneNumber.isBlank()?true:false;
        } catch (IncorrectResultSizeDataAccessException e) {
            return false;
        }
    }
    public int getUserId(String username){
        User user = userRepository.findByUserName(username);
        return user==null?-1:user.getId();
    }
    public User getUserByUserName(String username){
        User user = userRepository.findByUserName(username);
        return user==null?null:user;
    }

    public ResponseEntity<?> login(UserLogin userLogin) {

        if(userLogin.getUserName().contains("@")){
            User user1 = userRepository.findByEmail(userLogin.getUserName());
            userLogin.setUserName(user1.getUserName());
        }
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLogin.getUserName(), userLogin.getPassword())
            );

            UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
            User user = userPrinciple.getUser();

            if ("UnActive".equalsIgnoreCase(user.getStatus())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Your account has been suspended. Please contact support for further assistance.");
            }

            String token = jwtService.generateToken(user.getUserName(), user.getId());

            LoginResponse response = new LoginResponse(token, user.getUserName(), user.getFullName(), user.getRole(), user.getId(), "Registered successfully: Please complete your profile.",tokenExpire,user.getAddress());
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Incorrect username or password. Please try again.");
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Your session has expired. Please log in again.");
        }
         catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Login failed. Please check your credentials and try again.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again later.");
        }
    }

    public List<User> getAllUsers() {
        List<User> list= userRepository.findAll();
        for(User listUser:list){
            listUser.setPassword("***");
        }
        return list;
    }


    public Optional<User> getUserById(Integer id) {
       Optional<User> user= userRepository.findById(id);
       User user1=user.get();
       user1.setPassword("***");
       return user;
    }


    public void banUser(Integer id) {
        userRepository.banUser(id);
    }

    @Transactional
    public User updateUserRole(Integer id, String role) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setRole(role);
            user.setUpdateAt(Instant.now()); // Cập nhật thời gian hiện tại
            userRepository.save(user); // Lưu thay đổi
            return user; // Trả về đối tượng User đã cập nhật
        }
        return null; // Trả về null nếu không tìm thấy người dùng
    }



    public void activeUser(Integer id) {
        userRepository.activeUser(id);
    }

    public User updateProfile(ProfileUpdateRequest profileUpdateRequest, int userID) {
        Optional<User> userOptional = userRepository.findById(userID);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setUserName(profileUpdateRequest.getUserName());
            user.setFullName(profileUpdateRequest.getFullName());
            user.setPhoneNumber(profileUpdateRequest.getPhoneNumber());
            user.setAddress(profileUpdateRequest.getAddress());
            user.setUpdateAt(Instant.now());
            userRepository.save(user);
            return user;
        }
        return null;
    }
    public int getNewUsers(Integer day, Integer month, Integer year) {
        if (day != null && month != null && year != null) {
            return userRepository.countNewUsers(day, month, year);
        } else if (month != null && year != null) {
            return userRepository.countNewUsers(null,month, year);
        } else if (year != null) {
            return userRepository.countNewUsers(null,null,year);
        } else {
            return userRepository.countNewUsers(null,null,null);
        }
    }

    public Map<String, Long> getUserCountsByStatus(Integer day, Integer month, Integer year) {
        Map<String, Long> statusCounts = new HashMap<>();
        statusCounts.put("Active", userRepository.countByStatus("Active", day, month, year));
        statusCounts.put("UnActive", userRepository.countByStatus("UnActive", day, month, year));
        return statusCounts;
    }

    public Map<String, Long> getUserCountsByRole(Integer day, Integer month, Integer year) {
        Map<String, Long> roleCounts = new HashMap<>();
        // Thêm các role mà bạn muốn phân tích
        roleCounts.put("admin", userRepository.countByRole("admin", day, month, year));
        roleCounts.put("user", userRepository.countByRole("user", day, month, year));
        roleCounts.put("staff", userRepository.countByRole("staff", day, month, year));
        roleCounts.put("breeder", userRepository.countByRole("breeder", day, month, year));
        return roleCounts;
    }
    public List<UserAuctionCount> getTopBreedersByAuctionCount() {
        return userRepository.findTopBreedersByAuctionCount(PageRequest.of(0, 10));
    }

    public ResponseEntity<?> changePassword(Changepassword changepassword) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
            }

            UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
            User user = userPrinciple.getUser();

            if ("Admin".equals(user.getRole())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Admin does not have permission to change password");
            }

            if (!encoder.matches(changepassword.getOldPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Old password does not match");
            }

            user.setPassword(encoder.encode(changepassword.getNewPassword()));
            userRepository.save(user);

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
    private Integer generateOTP() {
        Random rand = new Random();
        return 100000 + rand.nextInt(900000); // OTP ngẫu nhiên 6 chữ số
    }

    private Date calculateExpirationDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5); // OTP hết hạn sau 5 phút
        return calendar.getTime();
    }

    private void sendOTPEmail(String email, Integer otp) {
        String subject = "Your OTP Code for Registration";
        StringBuilder body = new StringBuilder();
        body.append("<!DOCTYPE html>")
                .append("<html lang=\"vi\">")
                .append("<head><meta charset=\"UTF-8\" />")
                .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />")
                .append("<title>KOIAUCTION - OTP Email</title>")
                .append("<style>")
                .append("h1, p, div { margin: 0; padding: 0; font-family: \"Arial\", sans-serif; box-sizing: border-box; }")
                .append(".box { display: flex; justify-content: center; align-items: center; height: 100vh; width: 100%; }")
                .append(".container { max-width: 600px; width: 100%; background-color: white; border-radius: 8px; box-shadow: 0 2px 15px rgba(0, 0, 0, 0.1); padding: 24px; }")
                .append(".header { text-align: center; }")
                .append(".header h1 { font-size: 28px; font-weight: bold; color: #b41712; }")
                .append(".header p { font-size: 14px; color: #6b7280; margin-top: 8px; }")
                .append(".content { margin-top: 24px; }")
                .append(".content p { color: #374151; font-size: 16px; margin-bottom: 16px; }")
                .append(".otp-box { background-color: #f9fafb; padding: 24px; margin-bottom: 16px; text-align: center; border-radius: 8px; border: 1px solid #e5e7eb; }")
                .append(".otp-code { font-size: 36px; font-family: \"Courier New\", Courier, monospace; font-weight: bold; color: #374151; letter-spacing: 4px; }")
                .append(".otp-expiration { font-size: 14px !important; color: #374151bd !important; margin-top: 8px; }")
                .append(".footer { font-size: 12px; color: #9ca3af; text-align: center; margin-top: 24px; border-top: 1px solid #e5e7eb; padding-top: 16px; }")
                .append(".footer p { margin-bottom: 8px; font-size: 14px; opacity: 0.5; }")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<div class=\"box\">")
                .append("<div class=\"container\">")
                .append("<div class=\"header\">")
                .append("<h1>KOIAUCTION</h1>")
                .append("<p>Nền tảng đấu giá cá Koi trực tuyến</p>")
                .append("</div>")
                .append("<div class=\"content\">")
                .append("<p>Chào bạn,</p>")
                .append("<p>Mã OTP của bạn là: <span class=\"otp-code\">")
                .append(otp)
                .append("</span></p>")
                .append("<p class=\"otp-expiration\">Mã OTP sẽ hết hạn sau 5 phút.</p>")
                .append("</div>")
                .append("<div class=\"footer\">")
                .append("<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi.</p>")
                .append("</div>")
                .append("</div>")
                .append("</div>")
                .append("</body>")
                .append("</html>");

        emailService.sendHtmlMessage(new MailBody(email, subject, body.toString()));
    }



}