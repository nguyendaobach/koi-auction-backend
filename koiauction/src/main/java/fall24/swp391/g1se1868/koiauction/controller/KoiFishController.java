package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.KoiFish;
import fall24.swp391.g1se1868.koiauction.model.User;
import fall24.swp391.g1se1868.koiauction.model.UserPrinciple;
import fall24.swp391.g1se1868.koiauction.model.koifishdto.KoiFishDetailDTO;
import fall24.swp391.g1se1868.koiauction.model.koifishdto.KoiFishUser;
import fall24.swp391.g1se1868.koiauction.service.KoiFishService;
import fall24.swp391.g1se1868.koiauction.service.UserDetailService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/koi-fish")
public class KoiFishController {

    @Autowired
    private KoiFishService koiFishService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailService userDetailService;


    @GetMapping("/{id}")
    public ResponseEntity<KoiFishDetailDTO> getKoiFishById(@PathVariable Integer id) {
        return koiFishService.getById(id);
    }

    @GetMapping("/koi-active")
    public List<KoiFishUser> getKoiActive() {
        return koiFishService.getKoiActive();
    }

    @GetMapping()
    public List<KoiFishUser> getAll() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrinciple userPrinciple = null;

        // Kiểm tra xem người dùng đã xác thực chưa
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserPrinciple) {
            userPrinciple = (UserPrinciple) authentication.getPrincipal();
        }

        // Nếu người dùng đã xác thực
        if (userPrinciple != null) {
            User user = userPrinciple.getUser();

            // Nếu người dùng có vai trò ROLE_BREEDER, lấy danh sách cá Koi của Breeder
            if (user.getRole().equalsIgnoreCase("breeder")) {
                return koiFishService.getAllBreeder(user.getId());
            }
        }

        // Nếu không có người dùng xác thực hoặc không phải ROLE_BREEDER, trả về tất cả cá Koi
        return koiFishService.getAll();
    }

    @PostMapping(value = "/customize-koi-fish", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> customizeKoiFish(
            @RequestParam(name = "image-header", required = true) MultipartFile imageHeader,
            @RequestParam(name = "image-detail", required = true) List<MultipartFile> imageDetail,
            @RequestParam(name = "video", required = true) MultipartFile video,
            @RequestParam String name,
            @RequestParam BigDecimal weight,
            @RequestParam String sex,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthday,
            @RequestParam String description,
            @RequestParam BigDecimal length,
            @RequestParam Integer countryID,
            @RequestParam Integer koiTypeID
    ) {
        List<String> allowedFormats = Arrays.asList("image/png", "image/jpeg", "image/jpg", "image/gif", "image/bmp", "image/webp", "image/tiff");
        if (weight.compareTo(BigDecimal.ZERO) <= 0 || length.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Weight and Length must be positive values.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authenticated");
        }

        if (!authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_BREEDER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User does not have permission");
        }

        try {
            if (imageHeader.isEmpty()) {
                return ResponseEntity.badRequest().body("Header image is required.");
            }
            if (!allowedFormats.contains(imageHeader.getContentType().toLowerCase())) {
                return ResponseEntity.badRequest().body("Header image must be in PNG, JPEG, GIF, BMP, WEBP, or TIFF format.");
            }

            if (video.isEmpty()) {
                return ResponseEntity.badRequest().body("Video is required.");
            }
            if (!video.getContentType().toLowerCase().equals("video/mp4")) {
                return ResponseEntity.badRequest().body("Video must be in MP4 format.");
            }

            for (MultipartFile file : imageDetail) {
                if (file.isEmpty()) {
                    return ResponseEntity.badRequest().body("Each image in the detail must not be empty.");
                }
                String contentType = file.getContentType();
                if (!allowedFormats.contains(imageHeader.getContentType().toLowerCase())) {
                    return ResponseEntity.badRequest().body("Header image must be in PNG, JPEG, GIF, BMP, WEBP, or TIFF format.");
                }
            }

            // Lưu cá koi
            return koiFishService.saveKoiFish(imageHeader,imageDetail,video,name, weight, sex, birthday, description, length, countryID, koiTypeID);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping()
    public String delete(@RequestParam Integer id){
        return koiFishService.delete(id);
    }

}
