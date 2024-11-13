package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.model.koifishdto.Changepassword;
import fall24.swp391.g1se1868.koiauction.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    WalletService walletService;
    private BCryptPasswordEncoder encoder =new BCryptPasswordEncoder(12);

    public ResponseEntity<?> register(UserRegister userRegister) {
        if (userRegister == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User object cannot be null");
        }
        if (userRegister.getUserName() == null || userRegister.getUserName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username cannot be null or empty");
        }

        if (userRegister.getPassword() == null || userRegister.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password cannot be null or empty");
        }

        if (verifyUserName(userRegister.getUserName())) {
            if(verifyEmail(userRegister.getEmail())){
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
            }else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use");
            }

        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already in use");
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

}

