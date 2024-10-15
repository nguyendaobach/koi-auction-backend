package fall24.swp391.g1se1868.koiauction.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleTokenService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleTokenService.class);
    private final String clientId = "910756580458-ipk0vdo4sode9p86ngbhp4sc91fe2l5e.apps.googleusercontent.com"; // Thay thế bằng Client ID của bạn
    private final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

    public GoogleIdToken.Payload verifyToken(String token) throws IOException, GeneralSecurityException {
        // Kiểm tra token không rỗng
        if (token == null || token.trim().isEmpty()) {
            logger.error("Token không được để trống.");
            throw new IllegalArgumentException("Token không được để trống.");
        }

        // Tạo GoogleIdTokenVerifier
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), jsonFactory)
                .setAudience(Collections.singletonList(clientId)) // Đặt audience cho client ID của bạn
                .build();

        // Xác thực token
        GoogleIdToken idToken = verifier.verify(token);
        if (idToken == null) {
            logger.error("Token Google không hợp lệ.");
            throw new RuntimeException("Token Google không hợp lệ.");
        }

        logger.info("Token Google đã được xác thực thành công.");
        // Trả về payload của token
        return idToken.getPayload();
    }
}
