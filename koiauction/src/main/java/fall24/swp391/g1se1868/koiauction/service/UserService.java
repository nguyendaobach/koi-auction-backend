package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.User;
import fall24.swp391.g1se1868.koiauction.model.UserLogin;
import fall24.swp391.g1se1868.koiauction.model.UserPrinciple;
import fall24.swp391.g1se1868.koiauction.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    private BCryptPasswordEncoder encoder =new BCryptPasswordEncoder(12);

    public User register(User user){
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole("User");
        return userRepository.save(user);
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

    public String login(UserLogin userLogin) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLogin.getUserName(), userLogin.getPassword())
            );
            UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
            User user = userPrinciple.getUser();
            if ("banned".equalsIgnoreCase(user.getStatus())) {
                return "User is banned and cannot log in.";
            }
            return jwtService.generateToken(user.getUserName(), user.getId());
        } catch (AuthenticationException e) {
            return "Username or password is incorrect.";
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


    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }



}

