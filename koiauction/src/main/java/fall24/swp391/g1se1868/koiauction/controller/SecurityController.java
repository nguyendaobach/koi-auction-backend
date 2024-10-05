package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.User;
import fall24.swp391.g1se1868.koiauction.model.UserLogin;
import fall24.swp391.g1se1868.koiauction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/security")
public class SecurityController {
    @Autowired
    UserService userService;

    @PostMapping("/register")
    public String register(@RequestBody User user){
        return userService.register(user);
    }

    @PostMapping("/login")
    public String Login (@RequestBody UserLogin user){
        return userService.login(user);
    }

}
