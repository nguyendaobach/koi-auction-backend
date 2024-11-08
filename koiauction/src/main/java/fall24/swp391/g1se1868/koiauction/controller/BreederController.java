package fall24.swp391.g1se1868.koiauction.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/breeder")
public class BreederController {
    @GetMapping
    public String hello(){
    return("Hello Breeder");
}
}
