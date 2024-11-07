package fall24.swp391.g1se1868.koiauction;

import fall24.swp391.g1se1868.koiauction.model.*;
import fall24.swp391.g1se1868.koiauction.repository.KoiFishRepository;
import fall24.swp391.g1se1868.koiauction.repository.KoiMediaRepository;
import fall24.swp391.g1se1868.koiauction.repository.UserRepository;
import fall24.swp391.g1se1868.koiauction.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class KoiFishServiceTest {

    @Mock
    private KoiFishRepository koiFishRepository;

    @Mock
    private KoiMediaRepository koiMediaRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private SaveFileService firebaseService;

    @Mock
    private KoiTypeService koiTypeService;

    @Mock
    private KoiOriginService koiOriginService;

    @InjectMocks
    private KoiFishService koiFishService;

    private User mockUser;
    private KoiType mockKoiType;
    private KoiOrigin mockKoiOrigin;

    @BeforeEach
    void setUp() {
        // Tạo đối tượng giả lập (mock) cho User, KoiType và KoiOrigin
        mockUser = new User();
        mockUser.setId(1);
        mockUser.setFullName("Test User");

        mockKoiType = new KoiType();
        mockKoiType.setId(1);
        mockKoiType.setTypeName("Test Type");

        mockKoiOrigin = new KoiOrigin();
        mockKoiOrigin.setId(1);
        mockKoiOrigin.setCountry("Test Country");
    }

    @Test
    void testAddKoiFish_Success() throws Exception {
        // Arrange
        MultipartFile imageHeader = mock(MultipartFile.class);
        MultipartFile imageDetail = mock(MultipartFile.class);
        MultipartFile video = mock(MultipartFile.class);

        when(koiTypeService.getKoiTypeById(1)).thenReturn(Optional.of(mockKoiType));
        when(koiOriginService.getKoiOriginById(1)).thenReturn(Optional.of(mockKoiOrigin));
        when(firebaseService.uploadImage(any(MultipartFile.class))).thenReturn("http://example.com/image.jpg");
        when(koiFishRepository.save(any(KoiFish.class))).thenReturn(new KoiFish());

        // Act
        ResponseEntity<String> response = koiFishService.saveKoiFish(mockUser, imageHeader, List.of(imageDetail), video, "Koi Name", BigDecimal.TEN, "Male", LocalDate.now(), "Description", BigDecimal.ONE, 1, 1);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("KoiFish and KoiMedia saved successfully", response.getBody());
        verify(koiFishRepository, times(1)).save(any(KoiFish.class));
        verify(koiMediaRepository, times(3)).save(any(KoiMedia.class));
    }
    @Test
    void testAddKoiFish_Fail_KoiTypeNotFound() throws Exception {
        // Arrange
        MultipartFile imageHeader = mock(MultipartFile.class);
        MultipartFile imageDetail = mock(MultipartFile.class);
        MultipartFile video = mock(MultipartFile.class);

        when(koiTypeService.getKoiTypeById(1)).thenReturn(Optional.empty()); // Không tìm thấy loại cá

        // Act
        ResponseEntity<String> response = koiFishService.saveKoiFish(mockUser, imageHeader, List.of(imageDetail), video, "Koi Name", BigDecimal.TEN, "Male", LocalDate.now(), "Description", BigDecimal.ONE, 1, 1);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // Lỗi do không tìm thấy loại cá
        assertEquals("Invalid KoiType ID", response.getBody());
        verify(koiFishRepository, times(0)).save(any(KoiFish.class));
        verify(koiMediaRepository, times(0)).save(any(KoiMedia.class));
    }
    @Test
    void testAddKoiFish_Fail_KoiOriginNotFound() throws Exception {
        // Arrange
        MultipartFile imageHeader = mock(MultipartFile.class);
        MultipartFile imageDetail = mock(MultipartFile.class);
        MultipartFile video = mock(MultipartFile.class);

        when(koiTypeService.getKoiTypeById(1)).thenReturn(Optional.of(mockKoiType)); // Tìm thấy loại cá
        when(koiOriginService.getKoiOriginById(1)).thenReturn(Optional.empty()); // Không tìm thấy nguồn gốc cá

        // Act
        ResponseEntity<String> response = koiFishService.saveKoiFish(mockUser, imageHeader, List.of(imageDetail), video, "Koi Name", BigDecimal.TEN, "Male", LocalDate.now(), "Description", BigDecimal.ONE, 1, 1);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // Lỗi do không tìm thấy nguồn gốc cá
        assertEquals("Invalid KoiOrigin ID", response.getBody());
        verify(koiFishRepository, times(0)).save(any(KoiFish.class));
        verify(koiMediaRepository, times(0)).save(any(KoiMedia.class));
    }
    @Test
    void testAddKoiFish_Fail_UploadImageFailed() throws Exception {
        // Arrange
        MultipartFile imageHeader = mock(MultipartFile.class);
        MultipartFile imageDetail = mock(MultipartFile.class);
        MultipartFile video = mock(MultipartFile.class);

        when(koiTypeService.getKoiTypeById(1)).thenReturn(Optional.of(mockKoiType)); // Tìm thấy loại cá
        when(koiOriginService.getKoiOriginById(1)).thenReturn(Optional.of(mockKoiOrigin)); // Tìm thấy nguồn gốc cá
        when(firebaseService.uploadImage(any(MultipartFile.class))).thenThrow(new RuntimeException("Upload failed")); // Giả lập lỗi tải ảnh lên

        // Act
        ResponseEntity<String> response = koiFishService.saveKoiFish(mockUser, imageHeader, List.of(imageDetail), video, "Koi Name", BigDecimal.TEN, "Male", LocalDate.now(), "Description", BigDecimal.ONE, 1, 1);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()); // Lỗi do tải ảnh lên thất bại
        assertEquals("Failed to upload images", response.getBody());
        verify(koiFishRepository, times(0)).save(any(KoiFish.class));
        verify(koiMediaRepository, times(0)).save(any(KoiMedia.class));
    }
    @Test
    void testAddKoiFish_Fail_InvalidKoiFishData() throws Exception {
        // Arrange
        MultipartFile imageHeader = mock(MultipartFile.class);
        MultipartFile imageDetail = mock(MultipartFile.class);
        MultipartFile video = mock(MultipartFile.class);

        // Gửi dữ liệu không hợp lệ (ví dụ, tên là null)
        ResponseEntity<String> response = koiFishService.saveKoiFish(mockUser, imageHeader, List.of(imageDetail), video, null, BigDecimal.TEN, "Male", LocalDate.now(), "Description", BigDecimal.ONE, 1, 1);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // Lỗi do dữ liệu không hợp lệ
        assertEquals("Invalid input data", response.getBody());
        verify(koiFishRepository, times(0)).save(any(KoiFish.class));
        verify(koiMediaRepository, times(0)).save(any(KoiMedia.class));
    }
    @Test
    void testAddKoiFish_Fail_UserNotAuthorized() throws Exception {
        // Arrange
        MultipartFile imageHeader = mock(MultipartFile.class);
        MultipartFile imageDetail = mock(MultipartFile.class);
        MultipartFile video = mock(MultipartFile.class);

        // Giả lập người dùng không phải breeder
        User nonBreeder = new User();
        nonBreeder.setId(3); // Người dùng không phải breeder
        when(userService.getUserById(3)).thenReturn(Optional.of(nonBreeder));

// Act
        ResponseEntity<String> response = koiFishService.saveKoiFish(nonBreeder, imageHeader, List.of(imageDetail), video, "Koi Name", BigDecimal.TEN, "Male", LocalDate.now(), "Description", BigDecimal.ONE, 1, 1);

// Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode()); // Lỗi do không có quyền
        assertEquals("User is not authorized to add Koi", response.getBody());
        verify(koiFishRepository, times(0)).save(any(KoiFish.class));
        verify(koiMediaRepository, times(0)).save(any(KoiMedia.class));
    }


    @Test
    void testUpdateKoiFish_Success() throws Exception {
        // Arrange
        KoiFish existingKoiFish = new KoiFish();
        existingKoiFish.setId(1);
        existingKoiFish.setUserID(mockUser);

        when(koiFishRepository.findById(1)).thenReturn(Optional.of(existingKoiFish));
        when(koiTypeService.getKoiTypeById(1)).thenReturn(Optional.of(mockKoiType));
        when(koiOriginService.getKoiOriginById(1)).thenReturn(Optional.of(mockKoiOrigin));
        when(firebaseService.uploadImage(any(MultipartFile.class))).thenReturn("http://example.com/image.jpg");
        when(koiFishRepository.save(any(KoiFish.class))).thenReturn(existingKoiFish);

        MultipartFile imageHeader = mock(MultipartFile.class);
        MultipartFile imageDetail = mock(MultipartFile.class);
        MultipartFile video = mock(MultipartFile.class);

        // Act
        ResponseEntity<String> response = koiFishService.updateKoiFish(1, mockUser, imageHeader, List.of(imageDetail), video, "Updated Name", BigDecimal.ONE, "Female", LocalDate.now(), "Updated Description", BigDecimal.TEN, 1, 1, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("KoiFish updated successfully, new media added", response.getBody());
        verify(koiFishRepository, times(1)).save(any(KoiFish.class));
        verify(koiMediaRepository, times(3)).save(any(KoiMedia.class));
    }

    @Test
    void testDeleteKoiFish_Success() {
        // Arrange
        KoiFish koiFish = new KoiFish();
        koiFish.setId(1);
        koiFish.setUserID(mockUser);

        when(koiFishRepository.findById(1)).thenReturn(Optional.of(koiFish));


        ResponseEntity<?> response = koiFishService.delete(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("KoiFish deleted successfully", response.getBody());
        verify(koiFishRepository, times(1)).delete(koiFish);
    }

    @Test
    void testDeleteKoiFish_NotFound() {
        // Arrange
        when(koiFishRepository.findById(1)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = koiFishService.delete(1);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Koi Id not found", response.getBody());
        verify(koiFishRepository, times(0)).delete(any(KoiFish.class));
    }
}

