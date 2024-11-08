package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.KoiFish;
import fall24.swp391.g1se1868.koiauction.model.User;
import fall24.swp391.g1se1868.koiauction.model.UserPrinciple;
import fall24.swp391.g1se1868.koiauction.model.koifishdto.KoiFishDetailDTO;
import fall24.swp391.g1se1868.koiauction.model.koifishdto.KoiFishUser;
import fall24.swp391.g1se1868.koiauction.service.KoiFishService;
import fall24.swp391.g1se1868.koiauction.service.UserDetailService;
import fall24.swp391.g1se1868.koiauction.service.UserService;
import io.jsonwebtoken.io.IOException;
import jakarta.transaction.Transactional;
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
import java.util.Optional;

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

    @CrossOrigin(origins = "*") // Hoặc thay thế "*" bằng origin cụ thể
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

        // Kiểm tra giá trị weight và length
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

        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        Integer userId = userPrinciple.getId();

        User user = new User(userId) ;

        try {
            // Kiểm tra imageHeader
            if (imageHeader.isEmpty()) {
                return ResponseEntity.badRequest().body("Header image is required.");
            }
            if (!allowedFormats.contains(imageHeader.getContentType().toLowerCase())) {
                return ResponseEntity.badRequest().body("Header image must be in PNG, JPEG, GIF, BMP, WEBP, or TIFF format.");
            }

            // Kiểm tra video
            if (video.isEmpty()) {
                return ResponseEntity.badRequest().body("Video is required.");
            }
            if (!video.getContentType().toLowerCase().equals("video/mp4")) {
                return ResponseEntity.badRequest().body("Video must be in MP4 format.");
            }

            // Kiểm tra từng tệp trong imageDetail
            for (MultipartFile file : imageDetail) {
                if (file.isEmpty()) {
                    return ResponseEntity.badRequest().body("Each image in the detail must not be empty.");
                }
                String contentType = file.getContentType();
                if (!allowedFormats.contains(contentType.toLowerCase())) {
                    return ResponseEntity.badRequest().body("Each detail image must be in PNG, JPEG, GIF, BMP, WEBP, or TIFF format.");
                }
            }

            // Lưu cá koi
            return koiFishService.saveKoiFish(user,imageHeader, imageDetail, video, name, weight, sex, birthday, description, length, countryID, koiTypeID);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File processing error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = "*") // Hoặc thay thế "*" bằng origin cụ thể
    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateKoiFish(
            @RequestParam Integer koiFishId,
            @RequestParam(required = false) MultipartFile imageHeader,
            @RequestParam(required = false) List<MultipartFile> imageDetail,
            @RequestParam(required = false) MultipartFile video,
            @RequestParam String name,
            @RequestParam BigDecimal weight,
            @RequestParam String sex,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthday,
            @RequestParam String description,
            @RequestParam BigDecimal length,
            @RequestParam Integer countryID,
            @RequestParam Integer koiTypeID,
            @RequestParam(required = false) List<String> deleteUrls) {


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        Integer userId = userPrinciple.getId();

        User user = new User(userId);

        return koiFishService.updateKoiFish(
                koiFishId, user, imageHeader, imageDetail, video, name, weight, sex, birthday,
                description, length, countryID, koiTypeID, deleteUrls);
    }
    @DeleteMapping()
    public ResponseEntity<?> delete(@RequestParam Integer id){
        return koiFishService.delete(id);
    }

}
