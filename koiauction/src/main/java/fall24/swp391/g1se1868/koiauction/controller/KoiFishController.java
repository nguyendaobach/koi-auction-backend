package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.KoiFishUser;
import fall24.swp391.g1se1868.koiauction.model.User;
import fall24.swp391.g1se1868.koiauction.model.UserPrinciple;
import fall24.swp391.g1se1868.koiauction.service.KoiFishService;
import fall24.swp391.g1se1868.koiauction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/koi-fish")
public class KoiFishController {

    @Autowired
    private KoiFishService koiFishService;

    @Autowired
    private UserService userService;

    @GetMapping("/get-all")
    List<KoiFishUser> getAll(){
        return koiFishService.getAll();
    }



    @PostMapping(value = "/customize-koi-fish", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> customizeKoiFish(
            @RequestParam(name = "fileImg", required = true) List<MultipartFile> fileImg,
            @RequestParam BigDecimal weight,
            @RequestParam String sex,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthday,
            @RequestParam String description,
            @RequestParam BigDecimal length,
            @RequestParam Integer countryID,
            @RequestParam Integer koiTypeID
    ) {
        // Kiểm tra trọng lượng và chiều dài
        if (weight.compareTo(BigDecimal.ZERO) <= 0 || length.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Weight and Length must be positive values.");
        }

        // Kiểm tra xác thực người dùng
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authenticated");
        }

        if (!authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_BREEDER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User does not have permission");
        }

        try {
            // Kiểm tra kiểu tệp của từng fileImg
            for (MultipartFile file : fileImg) {
                String contentType = file.getContentType();
                if (contentType == null || (!contentType.toLowerCase().endsWith("image/png") && !contentType.toLowerCase().endsWith("video/mp4"))) {
                    return ResponseEntity.badRequest().body("All files must be either PNG images or MP4 videos.");
                }
            }

            // Lưu cá koi
            return koiFishService.saveKoiFish(fileImg, weight, sex, birthday, description, length, countryID, koiTypeID);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

}
