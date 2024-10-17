package fall24.swp391.g1se1868.koiauction.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.repository.UserRepository;
import fall24.swp391.g1se1868.koiauction.service.JwtService;
import fall24.swp391.g1se1868.koiauction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Random;

@RestController
@RequestMapping("/api/security")
public class SecurityController {
    @Autowired
    UserService userService;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder encoder =new BCryptPasswordEncoder(12);

    Random random = new Random();

    // Tạo số nguyên ngẫu nhiên
    String randomInt = String.valueOf(random.nextInt());

    @PostMapping("/register")
    public ResponseEntity<StringResponse> register(@RequestBody UserRegister user) {
        try {
            String message = userService.register(user);
            return new ResponseEntity<>(new StringResponse(message), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new StringResponse("Registration failed: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
        // Lấy token từ frontend
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

            User user= userRepository.findByEmail(email);

            if(user!=null){
                String token = jwtService.generateToken(user.getUserName(), user.getId());

                LoginResponse response = new LoginResponse(token, user.getUserName(), user.getFullName(), user.getRole(), user.getId(), "Registered successfully: Please complete your profile.");
                return ResponseEntity.ok(response);
            }else {
                User newUser = new User();
                newUser.setUserName(username);
                newUser.setFullName(displayName);
                newUser.setEmail(email);
                String password=encoder.encode(String.valueOf(randomInt));
                newUser.setPassword(encoder.encode(password));
                newUser.setCreateAt(Instant.now());
                newUser.setUpdateAt(Instant.now());
                newUser.setRole("User");
                newUser.setStatus("Active");
                userRepository.save(user);


                String token = jwtService.generateToken(newUser.getUserName(), newUser.getId());

                LoginResponse response = new LoginResponse(token, newUser.getUserName(), newUser.getFullName(), newUser.getRole(), newUser.getId(), "Registered successfully: Please complete your profile.");
                return ResponseEntity.ok(response);
            }


        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new StringResponse("Error at "+e.getMessage()));
        }
    }

}











