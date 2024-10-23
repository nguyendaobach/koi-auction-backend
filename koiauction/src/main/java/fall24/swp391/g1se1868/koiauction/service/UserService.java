package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    JwtService jwtService;

    @Autowired
    WalletService walletService;
    private BCryptPasswordEncoder encoder =new BCryptPasswordEncoder(12);

    public String register(UserRegister userRegister) {
        if (userRegister == null) {
            return "User object cannot be null";
        }

        if (userRegister.getUserName() == null || userRegister.getUserName().isEmpty()) {
            return "Username cannot be null or empty";
        }

        if (userRegister.getPassword() == null || userRegister.getPassword().isEmpty()) {
            return "Password cannot be null or empty";
        }

        if (verifyUserName(userRegister.getUserName())) {
            User user = new User();
            user.setUserName(userRegister.getUserName());
            user.setFullName(userRegister.getFullName());
            user.setPhoneNumber(userRegister.getPhoneNumber());
            user.setEmail(userRegister.getEmail());
            user.setPassword(encoder.encode(userRegister.getPassword()));
            user.setAddress(userRegister.getAddress());
            user.setCreateAt(Instant.now());
            user.setUpdateAt(Instant.now());
            user.setRole("User");
            user.setStatus("Active");

            if (userRepository.save(user) != null) {
                walletService.addUserWallet(user.getId());
                return "Registered successfully";
            } else {
                return "Registration failed";
            }

        } else {
            return "Username already in use";
        }
    }

    public boolean verifyUserName(String username){
        User user = userRepository.findByUserName(username);
        return user==null?true:false;
    }
    public boolean verifyEmail(String email){
        User user = userRepository.findByEmail(email);
        return user==null?true:false;
    }
    public boolean verifyPhoneNumber(String phoneNumber){
        User user = userRepository.findByPhoneNumber(phoneNumber);
        return user==null?true:false;
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

            LoginResponse response = new LoginResponse(token, user.getUserName(), user.getFullName(), user.getRole(), user.getId(), "Registered successfully: Please complete your profile.");
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


}

