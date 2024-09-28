package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.User;
import fall24.swp391.g1se1868.koiauction.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


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

    public static void main(String[] args) {
        UserService u = new UserService();
        System.out.println(u.getAllUsers().toString());
    }
}

