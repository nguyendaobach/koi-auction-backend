package fall24.swp391.g1se1868.koiauction.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PaymentSessionService {
    private final Map<String, Integer> paymentSessions = new ConcurrentHashMap<>();

    public String createPaymentSession(int userId) {
        String sessionId = generateUniqueSessionId();
        paymentSessions.put(sessionId, userId);
        return sessionId;
    }

    public Integer getUserIdFromSession(String sessionId) {
        return paymentSessions.remove(sessionId);
    }

    private String generateUniqueSessionId() {
        return UUID.randomUUID().toString();
    }
}
