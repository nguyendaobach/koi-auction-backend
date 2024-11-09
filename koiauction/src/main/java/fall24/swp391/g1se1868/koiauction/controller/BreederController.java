package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.UserAuctionCount;
import fall24.swp391.g1se1868.koiauction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/breeder")
public class BreederController {
    @Autowired
    UserService userService;

    @GetMapping("/user")
    public List<UserAuctionCount> getTopBreedersByAuctionCount() {
        return userService.getTopBreedersByAuctionCount();
}
}
