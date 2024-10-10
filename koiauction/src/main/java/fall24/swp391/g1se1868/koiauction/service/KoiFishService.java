package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.repository.KoiFishRepository;
import fall24.swp391.g1se1868.koiauction.repository.KoiMediaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KoiFishService {

    @Autowired
    private KoiFishRepository koiFishRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SaveFileService firebaseService;

    @Autowired
    private KoiTypeService koiTypeService;

    @Autowired
    private KoiOriginService koiOriginService;

    @Autowired
    private KoiMediaRepository koiMediaRepository;

    public KoiFish saveKoiFish(KoiFish koiFish) {
        return koiFishRepository.save(koiFish);
    }

    public List<KoiFishUser> getAll() {
        List<KoiFish> koiFishList = koiFishRepository.findAll();
        return koiFishList.stream()
                .map(koiFish -> {
                    User user = koiFish.getUserID();
                    UserKoifish userDTO = new UserKoifish(
                            user.getUserName(),
                            user.getFullName(),
                            user.getEmail(),
                            user.getAddress()
                    );
                    return new KoiFishUser(
                            koiFish.getId(),
                            userDTO,
                            koiFish.getCountryID().getId(),
                            koiFish.getKoiTypeID().getId(),
                            koiFish.getWeight(),
                            koiFish.getSex(),
                            koiFish.getBirthday(),
                            koiFish.getDescription(),
                            koiFish.getLength(),
                            koiFish.getStatus()
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public ResponseEntity<String> saveKoiFish(
            List<MultipartFile> fileImages,
            BigDecimal weight,
            String sex,
            LocalDate birthday,
            String description,
            BigDecimal length,
            Integer countryId,
            Integer koiTypeId) {

        try {
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

            Optional<User> optionalUser = userService.getUserById(userId);
            if (!optionalUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            User user = optionalUser.get();

            Optional<KoiType> optionalKoiType = koiTypeService.getKoiTypeById(koiTypeId);
            if (!optionalKoiType.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Koi Type not found");
            }
            KoiType koiType = optionalKoiType.get();

            Optional<KoiOrigin> optionalKoiOrigin = koiOriginService.getKoiOriginById(countryId);
            if (!optionalKoiOrigin.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Koi Origin not found");
            }
            KoiOrigin koiOrigin = optionalKoiOrigin.get();

            String status = "Active";
            KoiFish koiFish = new KoiFish(user, koiOrigin, koiType, weight, sex, birthday, description, length, status);
            KoiFish savedKoiFish = saveKoiFish(koiFish); // Lưu đối tượng KoiFish

            for (MultipartFile fileImage : fileImages) {
                if (fileImage != null) {
                    String contentType = fileImage.getContentType();
                    if (contentType == null || (!contentType.toLowerCase().endsWith("image/png") && !contentType.toLowerCase().endsWith("video/mp4"))) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File must be image or video");
                    }

                    String fileUrl = firebaseService.uploadImage(fileImage);
                    KoiMedia koiMedia = new KoiMedia();
                    koiMedia.setKoiID(savedKoiFish);
                    koiMedia.setUrl(fileUrl);
                    koiMedia.setMediaType(contentType.toLowerCase().endsWith("image/png") ? "Image" : "Video");

                    koiMediaRepository.save(koiMedia);
                }
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("KoiFish and KoiMedia saved successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading files: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while saving KoiFish: " + e.getMessage());
        }
    }


}
