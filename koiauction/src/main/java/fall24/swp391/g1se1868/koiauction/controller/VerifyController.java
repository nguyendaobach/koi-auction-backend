package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/verify")
public class VerifyController {

    @Autowired
    UserService userService;

    @GetMapping("/verifyUserName")
    public ResponseEntity<String> verifyUserName(@RequestParam String userName) {
        boolean isAvailable = userService.verifyUserName(userName);
        if (isAvailable) {
            return ResponseEntity.ok("Username is available");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username is already taken");
        }
    }
    @GetMapping("/verifyEmail")
    public ResponseEntity<String> verifyEmail(@RequestParam String Email) {
        boolean isAvailable = userService.verifyEmail(Email);
        if (isAvailable) {
            return ResponseEntity.ok("Email is available");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already taken");
        }
    }
    @GetMapping("/verifyPhone")
    public ResponseEntity<String> verifyPhone(@RequestParam String Phone) {
        boolean isAvailable = userService.verifyPhoneNumber(Phone);
        if (isAvailable) {
            return ResponseEntity.ok("Phone number is available");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Phone number is already taken");
        }
    }
}
