package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.KoiFish;
import fall24.swp391.g1se1868.koiauction.model.koifishdto.KoiFishDetailDTO;
import fall24.swp391.g1se1868.koiauction.model.koifishdto.KoiFishUser;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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


    @GetMapping("/{id}")
    public ResponseEntity<KoiFishDetailDTO> getKoiFishById(@PathVariable Integer id) {
        return koiFishService.getById(id);
    }

    @GetMapping("/get-koi-active")
    public List<KoiFishUser> getKoiActive(){
        return koiFishService.getKoiActive();
    }

    @PostMapping(value = "/customize-koi-fish", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> customizeKoiFish(
            @RequestParam(name = "image-header", required = true) MultipartFile imageHeader,
            @RequestParam(name = "image-detail", required = true) List<MultipartFile> imageDetail,
            @RequestParam(name = "video", required = true) MultipartFile video,
            @RequestParam BigDecimal weight,
            @RequestParam String sex,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthday,
            @RequestParam String description,
            @RequestParam BigDecimal length,
            @RequestParam Integer countryID,
            @RequestParam Integer koiTypeID
    ) {
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
            if (!imageHeader.getContentType().toLowerCase().equals("image/png")) {
                return ResponseEntity.badRequest().body("Header image must be in PNG format.");
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
                if (contentType == null || !contentType.toLowerCase().equals("image/png")) {
                    return ResponseEntity.badRequest().body("Each image detail must be in PNG format.");
                }
            }

            // Lưu cá koi
            return koiFishService.saveKoiFish(imageHeader,imageDetail,video, weight, sex, birthday, description, length, countryID, koiTypeID);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

}
