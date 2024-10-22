package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.model.koifishdto.KoiFishDetailDTO;
import fall24.swp391.g1se1868.koiauction.model.koifishdto.KoiMediaDTO;
import fall24.swp391.g1se1868.koiauction.model.koifishdto.KoiFishUser;
import fall24.swp391.g1se1868.koiauction.repository.KoiFishRepository;
import fall24.swp391.g1se1868.koiauction.repository.KoiMediaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
                    String fullName = koiFish.getUserID().getFullName();

                    String countryName = koiFish.getCountryID().getCountry();

                    String typeName = koiFish.getKoiTypeID().getTypeName();

                    Optional<KoiMedia> headerImage = koiMediaRepository.findByKoiIDAndMediaType(koiFish, "Header Image");

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
    }


    @Transactional
    public ResponseEntity<String> saveKoiFish(
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
            KoiFish savedKoiFish = saveKoiFish(koiFish); // Lưu đối tượng KoiFish

            // Lưu imageHeader là "Header Video"
            if (imageHeader != null) {
                String fileUrl = firebaseService.uploadImage(imageHeader);
                KoiMedia koiMedia = new KoiMedia();
                koiMedia.setKoiID(savedKoiFish);
                koiMedia.setUrl(fileUrl);
                koiMedia.setMediaType("Header Image");
                koiMediaRepository.save(koiMedia);
            }
            // Lưu imageDetail là "Image Detail"
            for (MultipartFile detailImage : imageDetail) {
                if (detailImage != null) {
                    String fileUrl = firebaseService.uploadImage(detailImage);
                    KoiMedia koiMedia = new KoiMedia();
                    koiMedia.setKoiID(savedKoiFish);
                    koiMedia.setUrl(fileUrl);
                    koiMedia.setMediaType("Image Detail");
                    koiMediaRepository.save(koiMedia);
                }
            }
            // Lưu video là "Video"
            if (video != null) {
                String fileUrl = firebaseService.uploadImage(video);
                KoiMedia koiMedia = new KoiMedia();
                koiMedia.setKoiID(savedKoiFish);
                koiMedia.setUrl(fileUrl);
                koiMedia.setMediaType("Video");
                koiMediaRepository.save(koiMedia);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("KoiFish and KoiMedia saved successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading files: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while saving KoiFish: " + e.getMessage());
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

    public List<KoiFishUser> getKoiActive() {
        // Lấy danh sách tất cả cá Koi từ repository
        List<KoiFish> koiFishList = koiFishRepository.findAll();

        // Lọc danh sách cá Koi theo trạng thái Active
        return koiFishList.stream()
                .filter(koiFish -> "Active".equalsIgnoreCase(koiFish.getStatus()))  // Lọc những cá Koi có trạng thái Active
                .map(koiFish -> {
                    String fullName = koiFish.getUserID().getFullName();
                    String countryName = koiFish.getCountryID().getCountry();
                    String typeName = koiFish.getKoiTypeID().getTypeName();
                    Optional<KoiMedia> headerImage = koiMediaRepository.findByKoiIDAndMediaType(koiFish, "Header Video");

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
                .collect(Collectors.toList());  // Trả về danh sách các cá Koi có trạng thái Active
    }


    public String delete(Integer id) {
        if(koiFishRepository.findById(id)!=null){
            koiFishRepository.delete(id);
            return "Koi Fish removed succesfully";
        }else {
            return "Koi Id not found";
        }
    }


    public List<KoiFishUser> getAllBreeder(Integer id) {

    List<KoiFish> koiFishList=koiFishRepository.findByUserID_Id(id);
        // Lọc danh sách cá Koi theo trạng thái Active
        return koiFishList.stream()
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
                .collect(Collectors.toList());  // Trả về danh sách các cá Koi có trạng thái Active
    }
}