package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
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
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLogin.getUserName(), userLogin.getPassword())
            );

            UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
            User user = userPrinciple.getUser();

            if ("banned".equalsIgnoreCase(user.getStatus())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is banned and cannot log in.");
            }

            String token = jwtService.generateToken(user.getUserName(), user.getId());

            LoginResponse response = new LoginResponse(token, user.getUserName(),user.getFullName(),user.getRole());
            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username or password is incorrect.");
        }
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }


    public User createUser(User user) {
        user.setCreateAt(new Date().toInstant()); // Gán thời gian hiện tại
        user.setUpdateAt(new Date().toInstant());
        return userRepository.save(user);
    }


    public User updateUser(Long id, User updatedUser) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setRole(updatedUser.getRole());
            existingUser.setUserName(updatedUser.getUserName());
            existingUser.setFullName(updatedUser.getFullName());
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setPassword(updatedUser.getPassword());
            existingUser.setAddress(updatedUser.getAddress());
            existingUser.setUpdateAt(new Date().toInstant()); // Gán lại thời gian cập nhật
            existingUser.setStatus(updatedUser.getStatus());
            return userRepository.save(existingUser);
        }
        return null;
    }

    public void banUser(Long id) {
        userRepository.banUser(id);
    }

    @Transactional
    public User updateUserRole(Long id, String role) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setRole(role);
            return userRepository.save(user);
        }
        return null;
    }


}

