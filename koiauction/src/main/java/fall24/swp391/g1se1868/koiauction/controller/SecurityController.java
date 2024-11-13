package fall24.swp391.g1se1868.koiauction.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.model.koifishdto.Changepassword;
import fall24.swp391.g1se1868.koiauction.repository.UserRepository;
import fall24.swp391.g1se1868.koiauction.service.*;
import io.jsonwebtoken.security.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/security")
public class SecurityController {
    @Value("${token.expire}")
    private int tokenExpire;
    @Autowired
    UserService userService;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private RegisterService registerService;

    private BCryptPasswordEncoder encoder =new BCryptPasswordEncoder(12);

    Random random = new Random();

    String randomInt = String.valueOf(random.nextInt());

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegister user) {
        try {
            return userService.register(user);
        } catch (Exception e) {
            return new ResponseEntity<>(new StringResponse("Registration failed: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOTP(@RequestBody UserRegister userRegister, @RequestParam Integer otp) {
        Register register = registerService.getOTPByEmail(userRegister.getEmail());
        if (register == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found.");
        }

        if (register.getOtp().equals(otp)) {
            if (registerService.isOTPExpired(register.getExpirationTime())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP has expired.");
            }
            User user = new User();
            user.setUserName(userRegister.getUserName());
            user.setEmail(userRegister.getEmail());
            user.setPassword(encoder.encode(userRegister.getPassword()));
            user.setFullName("");
            user.setPhoneNumber("");
            user.setAddress("");
            user.setCreateAt(Instant.now());
            user.setUpdateAt(Instant.now());
            user.setRole("User");
            user.setStatus("Active");
            if (userRepository.save(user) != null) {
                walletService.addUserWallet(user.getId());
                return ResponseEntity.status(HttpStatus.CREATED).body("Registered successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP.");
        }
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody Changepassword changepassword){
        return userService.changePassword(changepassword);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLogin user) {
        return userService.login(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<StringResponse> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        String username = userPrinciple.getUsername();
        String newToken = jwtService.generateToken(username, 1);

        return ResponseEntity.ok(new StringResponse("successfully logged out"));
    }

    @PostMapping("/google/login")
    public ResponseEntity<?> handleGoogleLogin(@RequestBody String token) {
        return getGoogleProfile(token);
    }

    private ResponseEntity<?> getGoogleProfile(String token) {
        // Tạo một đối tượng RestTemplate để gửi request HTTP
        RestTemplate restTemplate = new RestTemplate();

        // Đặt headers với Authorization: Bearer <token>
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); // Bearer <token>
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // URL của API Google People
        String url = "https://people.googleapis.com/v1/people/me?personFields=names,photos,emailAddresses";

        // Gửi request GET đến API của Google
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        // Parse JSON từ response
        return parseProfileResponse(response.getBody());
    }
    private ResponseEntity<?> parseProfileResponse(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);

            // Lấy email
            String email = rootNode.path("emailAddresses").get(0).path("value").asText();

            String username = email.split("@")[0];
            // Lấy tên
            String displayName = rootNode.path("names").get(0).path("displayName").asText();

            // Lấy URL ảnh
            String photoUrl = rootNode.path("photos").get(0).path("url").asText();

            User user = userRepository.findByEmail(email);

            if (user != null) {
                String token = jwtService.generateToken(user.getUserName(), user.getId());

                LoginResponse response = new LoginResponse(token, user.getUserName(), user.getFullName(), user.getRole(), user.getId(), "Registered successfully: Please complete your profile.", tokenExpire,user.getAddress());
                return ResponseEntity.ok(response);
            } else {
                User newUser = new User();
                newUser.setUserName(username);
                newUser.setFullName(displayName);
                newUser.setEmail(email);
                String password = encoder.encode(String.valueOf(randomInt));
                newUser.setPassword(password); // Không cần mã hóa lại
                newUser.setRole("User");
                newUser.setStatus("Active");
                newUser.setCreateAt(Instant.now());
                newUser.setUpdateAt(Instant.now());
                newUser.setPhoneNumber("");
                newUser.setAddress("");

                if (userRepository.save(newUser) != null) {
                    walletService.addUserWallet(newUser.getId());
                    // Tạo token cho người dùng mới
                }
                String token = jwtService.generateToken(newUser.getUserName(), newUser.getId());
                LoginResponse response = new LoginResponse(token, newUser.getUserName(), newUser.getFullName(), newUser.getRole(), newUser.getId(), "User created successfully. Please complete your profile.", tokenExpire,user.getAddress());
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            // Xử lý ngoại lệ
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing profile response");
        }
    }
}











