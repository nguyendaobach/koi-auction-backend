package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.StringResponse;
import fall24.swp391.g1se1868.koiauction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/verify")
public class VerifyController {

    @Autowired
    UserService userService;

    @GetMapping("/verify-userName")
    public ResponseEntity<StringResponse> verifyUserName(@RequestParam String userName) {
        boolean isAvailable = userService.verifyUserName(userName);
        if (isAvailable) {
            return ResponseEntity.ok(new StringResponse("Username is available"));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new StringResponse("Username is already taken"));
        }
    }
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    @GetMapping("/verify-email")
    public ResponseEntity<StringResponse> verifyEmail(@RequestParam String Email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(Email);
        if(matcher.matches()) {
            boolean isAvailable = userService.verifyEmail(Email);
            if (isAvailable) {
                return ResponseEntity.ok(new StringResponse("Email is available"));
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new StringResponse("Email is already taken"));
            }
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StringResponse("Email is invalid format"));
        }
    }
    @GetMapping("/verify-phone")
    public ResponseEntity<StringResponse> verifyPhone(@RequestParam String Phone) {
        String allCountryRegex = "^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$";
            if (Phone.matches(allCountryRegex)) {
                boolean isAvailable = userService.verifyPhoneNumber(Phone);
                if (isAvailable) {
                    return ResponseEntity.ok(new StringResponse("Phone number is available"));
                } else {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(new StringResponse("Phone number is already taken"));
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StringResponse("Phone number is invalid format"));
            }
        }
    }

