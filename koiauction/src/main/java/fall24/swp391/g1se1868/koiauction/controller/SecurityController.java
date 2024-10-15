package fall24.swp391.g1se1868.koiauction.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.repository.UserRepository;
import fall24.swp391.g1se1868.koiauction.service.GoogleTokenService;
import fall24.swp391.g1se1868.koiauction.service.JwtService;
import fall24.swp391.g1se1868.koiauction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/security")
public class SecurityController {
    @Autowired
    UserService userService;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoogleTokenService googleTokenService;
    private BCryptPasswordEncoder encoder =new BCryptPasswordEncoder(12);

    @PostMapping("/register")
    public StringResponse register(@RequestBody UserRegister user){
        return new StringResponse(userService.register(user));
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
//    @GetMapping("/login-google")
//    public ResponseEntity<?> handleGoogleLogin(@RequestParam("access_token") String accessToken) {
//        try {
//            // Xác thực Access Token và lấy thông tin người dùng
//            GoogleIdToken.Payload payload = googleTokenService.verifyToken(accessToken);
//            String email = payload.getEmail();
//            String fullName = (String) payload.get("name");
//
//            // Tìm kiếm người dùng trong cơ sở dữ liệu bằng email
//            User existingUser = userRepository.findByEmail(email);
//
//            if (existingUser != null) {
//                // Cập nhật thời gian đăng nhập cho người dùng đã tồn tại
//                existingUser.setUpdateAt(Instant.now());
//                userRepository.save(existingUser);
//
//                // Tạo JWT token cho người dùng
//                String jwtToken = jwtService.generateToken(existingUser.getUserName(), existingUser.getId());
//                return ResponseEntity.ok(new LoginResponse(jwtToken, existingUser.getUserName(), existingUser.getFullName(), existingUser.getRole(), existingUser.getId(), "Login successful."));
//            } else {
//                // Tạo người dùng mới nếu chưa tồn tại
//                User newUser = new User();
//                newUser.setUserName(fullName);
//                newUser.setFullName(fullName);
//                newUser.setEmail(email);
//                newUser.setPassword(encoder.encode(UUID.randomUUID().toString())); // Mật khẩu ngẫu nhiên
//                newUser.setCreateAt(Instant.now());
//                newUser.setUpdateAt(Instant.now());
//                newUser.setRole("User");
//                newUser.setStatus("Active");
//
//                userRepository.save(newUser);
//
//                String jwtToken = jwtService.generateToken(newUser.getUserName(), newUser.getId());
//                return ResponseEntity.ok(new LoginResponse(jwtToken, newUser.getUserName(), newUser.getFullName(), newUser.getRole(), newUser.getId(), "Registration and login successful."));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StringResponse("Login failed: " + e.getMessage()));
//        }
//    }
@GetMapping("/login-google")
public ResponseEntity<?> handleGoogleLogin(@RequestParam("access_token") String accessToken) {
    try {
        // Ghi lại Access Token để kiểm tra
        System.out.println("Access Token: " + accessToken);

        // Xác thực Access Token và lấy thông tin người dùng
        GoogleIdToken.Payload payload = googleTokenService.verifyToken(accessToken);
        String email = payload.getEmail();
        String fullName = (String) payload.get("name");

        // Tìm kiếm người dùng trong cơ sở dữ liệu bằng email
        User existingUser = userRepository.findByEmail(email);

        if (existingUser != null) {
            // Cập nhật thời gian đăng nhập cho người dùng đã tồn tại
            existingUser.setUpdateAt(Instant.now());
            userRepository.save(existingUser);

            // Tạo JWT token cho người dùng
            String jwtToken = jwtService.generateToken(existingUser.getUserName(), existingUser.getId());
            return ResponseEntity.ok(new LoginResponse(jwtToken, existingUser.getUserName(), existingUser.getFullName(), existingUser.getRole(), existingUser.getId(), "Login successful."));
        } else {
            // Tạo người dùng mới nếu chưa tồn tại
            User newUser = new User();
            newUser.setUserName(fullName);
            newUser.setFullName(fullName);
            newUser.setEmail(email);
            newUser.setPassword(encoder.encode(UUID.randomUUID().toString())); // Mật khẩu ngẫu nhiên
            newUser.setCreateAt(Instant.now());
            newUser.setUpdateAt(Instant.now());
            newUser.setRole("User");
            newUser.setStatus("Active");

            userRepository.save(newUser);

            String jwtToken = jwtService.generateToken(newUser.getUserName(), newUser.getId());
            return ResponseEntity.ok(new LoginResponse(jwtToken, newUser.getUserName(), newUser.getFullName(), newUser.getRole(), newUser.getId(), "Registration and login successful."));
        }
    } catch (IllegalArgumentException e) {
        // Xử lý lỗi liên quan đến token không hợp lệ
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StringResponse("Invalid token: " + e.getMessage()));
    } catch (Exception e) {
        // Xử lý lỗi chung
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StringResponse("Login failed: " + e.getMessage()));
    }
}


}











