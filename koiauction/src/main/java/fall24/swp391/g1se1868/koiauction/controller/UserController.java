package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.StringResponse;
import fall24.swp391.g1se1868.koiauction.model.User;
import fall24.swp391.g1se1868.koiauction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin-manager/users")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/getAll")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }


    @GetMapping("/get-user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/roles")
    public ResponseEntity<List<String>> getAvailableRoles() {
        List<String> roles = Arrays.asList("Admin", "User", "Staff", "Breeder"); // Danh sách vai trò có sẵn
        return ResponseEntity.ok(roles);
    }


    @PostMapping("/active-user/{id}")
    public ResponseEntity<User> activeUser(@PathVariable Integer id) {
        userService.activeUser(id);
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/ban-user/{id}")
    public ResponseEntity<User> banUser(@PathVariable Integer id) {
        userService.banUser(id);
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/update-role/{id}")
    public ResponseEntity<User> updateRole(@PathVariable Integer id, @RequestParam String role) {
        // Kiểm tra vai trò hợp lệ
        if (!isValidRole(role)) {
            return ResponseEntity.badRequest().body(null); // Trả về lỗi nếu vai trò không hợp lệ
        }

        // Cập nhật vai trò người dùng
        User updatedUser = userService.updateUserRole(id, role);

        // Trả về phản hồi dựa trên kết quả cập nhật
        return updatedUser != null
                ? ResponseEntity.ok(updatedUser) // Trả về đối tượng User đã cập nhật
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(updatedUser); // Trả về 404 nếu không tìm thấy người dùng
    }



    private boolean isValidRole(String role) {
        return role.equals("User") || role.equals("Breeder") || role.equals("Staff") || role.equals("Admin");
    }

}