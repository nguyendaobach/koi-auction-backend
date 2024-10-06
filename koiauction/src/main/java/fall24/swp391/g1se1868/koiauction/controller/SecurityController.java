package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.User;
import fall24.swp391.g1se1868.koiauction.model.UserLogin;
import fall24.swp391.g1se1868.koiauction.model.UserPrinciple;
import fall24.swp391.g1se1868.koiauction.model.UserRegister;
import fall24.swp391.g1se1868.koiauction.service.JwtService;
import fall24.swp391.g1se1868.koiauction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/security")
public class SecurityController {
    @Autowired
    UserService userService;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public String register(@RequestBody UserRegister user){
        return userService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLogin user) {
        return userService.login(user);
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        String username = userPrinciple.getUsername();
        String newToken = jwtService.generateToken(username, 1);

        return ResponseEntity.ok("successfully logged out");
    }

}
