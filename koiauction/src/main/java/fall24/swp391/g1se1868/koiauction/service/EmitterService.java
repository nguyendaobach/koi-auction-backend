package fall24.swp391.g1se1868.koiauction.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmitterService {

    private final Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();

    // Create an emitter and store it by auctionId or userId, depending on your use case
    public SseEmitter createEmitter(Integer id) {
        SseEmitter emitter = new SseEmitter(3600000L); // 1 hour timeout
        emitters.put(id, emitter);
        emitter.onCompletion(() -> emitters.remove(id));
        emitter.onTimeout(() -> emitters.remove(id));
        return emitter;
    }

    // Send updates to the specific emitter by auctionId
    public void sendUpdate(Integer id, Object data) {
        SseEmitter emitter = emitters.get(id);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("auction-update").data(data));
            } catch (IOException e) {
                emitters.remove(id);
            }
        }
    }

    // Remove emitter when auction is closed or completed
    public void removeEmitter(Integer id) {
        emitters.remove(id);
    }
}

