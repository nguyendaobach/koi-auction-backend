package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.model.koifishdto.KoiActiveResponse;
import fall24.swp391.g1se1868.koiauction.model.koifishdto.KoiFishDetailDTO;
import fall24.swp391.g1se1868.koiauction.model.koifishdto.KoiFishIdName;
import fall24.swp391.g1se1868.koiauction.model.koifishdto.KoiMediaDTO;
import fall24.swp391.g1se1868.koiauction.model.koifishdto.KoiFishUser;
import fall24.swp391.g1se1868.koiauction.repository.KoiFishRepository;
import fall24.swp391.g1se1868.koiauction.repository.KoiMediaRepository;
import fall24.swp391.g1se1868.koiauction.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private UserRepository userRepository;

    public KoiFish saveKoiFish(KoiFish koiFish) {
        return koiFishRepository.save(koiFish);
    }

    public Map<String, Object> getAll(Pageable pageable) {
        // Lấy danh sách cá Koi với phân trang
        Page<KoiFish> koiFishPage = koiFishRepository.findAll(pageable);

        // Chuyển đổi danh sách KoiFish thành KoiFishUser với các thông tin cần thiết
        List<KoiFishUser> koiFishUserList = koiFishPage.getContent().stream()
                .map(koiFish -> {
                    String fullName = koiFish.getUserID().getFullName();
                    String countryName = koiFish.getCountryID().getCountry();
                    String typeName = koiFish.getKoiTypeID().getTypeName();

                    Optional<KoiMedia> headerImage = koiMediaRepository.findByKoiIDAndMediaType(koiFish, "Header Image");

                    // Trả về đối tượng KoiFishUser với thông tin cá Koi và hình ảnh
                    return new KoiFishUser(
                            koiFish.getId(),
                            koiFish.getKoiName(),
                            fullName,  // Lấy fullName của user
                            countryName,  // Lấy tên nước thay vì ID
                            typeName,  // Lấy tên loại cá thay vì ID
                            koiFish.getWeight(),
                            koiFish.getSex(),
                            koiFish.getBirthday(),
                            koiFish.getDescription(),
                            koiFish.getLength(),
                            koiFish.getStatus(),
                            headerImage.isPresent() ? headerImage.get().getUrl() : null  // URL hình ảnh header
                    );
                })
                .collect(Collectors.toList());

        // Tạo đối tượng Map để trả về thông tin phân trang và danh sách cá Koi
        Map<String, Object> response = new HashMap<>();
        response.put("koi", koiFishUserList);  // Danh sách cá Koi
        response.put("currentPage", koiFishPage.getNumber());  // Trang hiện tại
        response.put("totalPages", koiFishPage.getTotalPages());  // Tổng số trang
        response.put("totalElements", koiFishPage.getTotalElements());  // Tổng số phần tử

        return response;  // Trả về đối tượng Map chứa thông tin phân trang và danh sách cá Koi
    }

    @Transactional
    public ResponseEntity<String> saveKoiFish(
            User user,
            String imageHeader,
            List<String> imageDetail,
            String video,
            String name,
            BigDecimal weight,
            String sex,
            LocalDate birthday,
            String description,
            BigDecimal length,
            Integer countryId,
            Integer koiTypeId) {

        try {
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
            KoiFish koiFish = new KoiFish(user,name, koiOrigin, koiType, weight, sex, birthday, description, length, status);
            KoiFish savedKoiFish = saveKoiFish(koiFish);

            if (imageHeader != null) {

                KoiMedia koiMedia = new KoiMedia();
                koiMedia.setKoiID(savedKoiFish);
                koiMedia.setUrl(imageHeader);
                koiMedia.setMediaType("Header Image");
                koiMediaRepository.save(koiMedia);
            }
            for (String detailImage : imageDetail) {
                if (detailImage != null) {
                    KoiMedia koiMedia = new KoiMedia();
                    koiMedia.setKoiID(savedKoiFish);
                    koiMedia.setUrl(detailImage);
                    koiMedia.setMediaType("Image Detail");
                    koiMediaRepository.save(koiMedia);
                }
            }
            if (video != null) {
                KoiMedia koiMedia = new KoiMedia();
                koiMedia.setKoiID(savedKoiFish);
                koiMedia.setUrl(video);
                koiMedia.setMediaType("Video");
                koiMediaRepository.save(koiMedia);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("KoiFish and KoiMedia saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while saving KoiFish: " + e.getMessage());
        }
    }
    @Transactional
    public ResponseEntity<String> updateKoiFish(
            Integer koiFishId,
            User user,
            MultipartFile imageHeader,
            List<MultipartFile> imageDetail,
            MultipartFile video,
            String name,
            BigDecimal weight,
            String sex,
            LocalDate birthday,
            String description,
            BigDecimal length,
            Integer countryId,
            Integer koiTypeId,
            List<String> deleteUrls) {

        try {
            Optional<KoiFish> optionalKoiFish = koiFishRepository.findById(koiFishId);
            if (!optionalKoiFish.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Koi Fish not found");
            }
            KoiFish koiFish = optionalKoiFish.get();

            if (!koiFish.getUserID().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to update this Koi Fish");
            }

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

            koiFish.setUserID(user);
            koiFish.setKoiName(name);
            koiFish.setCountryID(koiOrigin);
            koiFish.setKoiTypeID(koiType);
            koiFish.setWeight(weight);
            koiFish.setSex(sex);
            koiFish.setBirthday(birthday);
            koiFish.setDescription(description);
            koiFish.setLength(length);
            koiFishRepository.save(koiFish);

            if (deleteUrls != null && !deleteUrls.isEmpty()) {
                for (String url : deleteUrls) {
                    koiMediaRepository.deleteByUrl(url);
                }
            }
            if (imageHeader != null) {
                String fileUrl = firebaseService.uploadImage(imageHeader);
                if (fileUrl != null) {
                    KoiMedia koiMedia = new KoiMedia();
                    koiMedia.setKoiID(koiFish);
                    koiMedia.setUrl(fileUrl);
                    koiMedia.setMediaType("Header Image");
                    koiMediaRepository.save(koiMedia);
                }
            }
            if (imageDetail != null && !imageDetail.isEmpty()) {
                for (MultipartFile detailImage : imageDetail) {
                    if (detailImage != null) {
                        String fileUrl = firebaseService.uploadImage(detailImage);
                        if (fileUrl != null) {
                            KoiMedia koiMedia = new KoiMedia();
                            koiMedia.setKoiID(koiFish);
                            koiMedia.setUrl(fileUrl);
                            koiMedia.setMediaType("Image Detail");
                            koiMediaRepository.save(koiMedia);
                        }
                    }
                }
            }
            if (video != null) {
                String fileUrl = firebaseService.uploadImage(video);
                if (fileUrl != null) {
                    KoiMedia koiMedia = new KoiMedia();
                    koiMedia.setKoiID(koiFish);
                    koiMedia.setUrl(fileUrl);
                    koiMedia.setMediaType("Video");
                    koiMediaRepository.save(koiMedia);
                }
            }

            return ResponseEntity.status(HttpStatus.OK).body("KoiFish updated successfully, new media added");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading files: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating KoiFish: " + e.getMessage());
        }
    }

    public ResponseEntity<KoiFishDetailDTO> getById(Integer id) {
        Optional<KoiFish> koiFishOpt = koiFishRepository.findById(id);

        if (!koiFishOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        KoiFish koiFish = koiFishOpt.get();

        String creatorFullName = koiFish.getUserID().getFullName();

        KoiType koiType = koiFish.getKoiTypeID();

        KoiOrigin koiOrigin=koiFish.getCountryID();

        List<KoiMediaDTO> mediaList = koiMediaRepository.findByKoiID(koiFish)
                .stream()
                .map(media -> new KoiMediaDTO(media.getMediaType(), media.getUrl()))
                .collect(Collectors.toList());

        KoiFishDetailDTO koiFishDetail = new KoiFishDetailDTO(
                koiFish.getId(),
                koiFish.getKoiName(),
                creatorFullName,
                koiType.getTypeName(),
                koiOrigin.getCountry(),
                koiFish.getWeight(),
                koiFish.getSex(),
                koiFish.getBirthday(),
                koiFish.getDescription(),
                koiFish.getLength(),
                koiFish.getStatus(),
                mediaList
        );

        return ResponseEntity.ok(koiFishDetail);
    }

public List<KoiFishIdName> getKoiActive() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<KoiFish> koiFishList;

        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserPrinciple) {
            UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
            User userId = userPrinciple.getUser();

            String role = userId.getRole();
            if ("Breeder".equals(role)) {
                koiFishList = koiFishRepository.findByUserID_Id(userId.getId());
            } else {
                koiFishList = koiFishRepository.findAll();
            }
        } else {
            koiFishList = koiFishRepository.findAll();
        }

        return koiFishList.stream()
                .filter(koiFish -> "Active".equalsIgnoreCase(koiFish.getStatus()))
                .map(koiFish -> new KoiFishIdName(koiFish.getId(), koiFish.getKoiName()))
                .collect(Collectors.toList());
    }

    public ResponseEntity<?> delete(Integer id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        Integer userId = userPrinciple.getId();

        Optional<KoiFish> koiFishOpt = koiFishRepository.findById(id);
        if (koiFishOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Koi Fish not found");
        }

        KoiFish koiFish = koiFishOpt.get();

        if (!koiFish.getUserID().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User has no permission");
        }

        if (koiFish.getStatus().equals("Pending") || koiFish.getStatus().equals("Ongoing")) {
            koiFishRepository.delete(koiFish);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Koi Fish removed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Koi Fish cannot be removed");
        }
    }

    public Map<String, Object> getAllBreeder(Integer id, Pageable pageable) {
        // Lấy danh sách cá Koi của Breeder với phân trang
        Page<KoiFish> koiFishPage = koiFishRepository.findByUserID_Id(id, pageable);

        // Lọc danh sách cá Koi theo trạng thái "Active"
        List<KoiFishUser> koiFishUserList = koiFishPage.getContent().stream()
                .filter(koiFish -> "Active".equalsIgnoreCase(koiFish.getStatus()))  // Lọc những cá Koi có trạng thái Active
                .map(koiFish -> {
                    String fullName = koiFish.getUserID().getFullName();
                    String countryName = koiFish.getCountryID().getCountry();
                    String typeName = koiFish.getKoiTypeID().getTypeName();
                    Optional<KoiMedia> headerImage = koiMediaRepository.findByKoiIDAndMediaType(koiFish, "Header Image");

                    // Trả về đối tượng KoiFishUser với thông tin cá Koi và hình ảnh
                    return new KoiFishUser(
                            koiFish.getId(),
                            koiFish.getKoiName(),
                            fullName,  // Lấy fullName của user
                            countryName,  // Lấy tên nước thay vì ID
                            typeName,  // Lấy tên loại cá thay vì ID
                            koiFish.getWeight(),
                            koiFish.getSex(),
                            koiFish.getBirthday(),
                            koiFish.getDescription(),
                            koiFish.getLength(),
                            koiFish.getStatus(),
                            headerImage.isPresent() ? headerImage.get().getUrl() : null  // URL hình ảnh header
                    );
                })
                .collect(Collectors.toList());  // Lọc và chuyển đổi thành danh sách các đối tượng KoiFishUser

        // Tạo đối tượng Map để trả về thông tin phân trang và danh sách cá Koi
        Map<String, Object> response = new HashMap<>();
        response.put("koi", koiFishUserList);  // Danh sách cá Koi có trạng thái "Active"
        response.put("currentPage", koiFishPage.getNumber());  // Trang hiện tại
        response.put("totalPages", koiFishPage.getTotalPages());  // Tổng số trang
        response.put("totalElements", koiFishPage.getTotalElements());  // Tổng số phần tử

        return response;  // Trả về đối tượng Map chứa thông tin phân trang và danh sách cá Koi
    }


    public ResponseEntity<?> saveFile(MultipartFile file) throws IOException {
        // Kiểm tra xác thực người dùng
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authenticated");
        }

        String filee=firebaseService.uploadImage(file);

        return ResponseEntity.status(HttpStatus.OK).body(filee);
    }
}