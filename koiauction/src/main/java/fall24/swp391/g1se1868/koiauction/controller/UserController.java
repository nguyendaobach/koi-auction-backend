package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.User;
import fall24.swp391.g1se1868.koiauction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    @PostMapping("/ban-user/{id}")
    public ResponseEntity<User> banUser(@PathVariable Integer id) {
        userService.banUser(id);
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/update-role/{id}")
    public ResponseEntity<User> updateRole(@PathVariable Integer id, @RequestParam String role) {

        if (!isValidRole(role)) {
            return ResponseEntity.badRequest().body(null);
        }


        User user = userService.updateUserRole(id, role);

        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    private boolean isValidRole(String role) {
        return role.equals("User") || role.equals("Breeder") || role.equals("Staff") || role.equals("Admin");
    }

}