package fall24.swp391.g1se1868.koiauction.config;

import fall24.swp391.g1se1868.koiauction.service.JwtService;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    JwtService jwtService;
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // Topic for message broadcasting
        config.setApplicationDestinationPrefixes("/app"); // Prefix for client-side sending
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // WebSocket endpoint
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor != null && accessor.getCommand() == StompCommand.CONNECT) {
                    String token = accessor.getFirstNativeHeader("Authorization");
                    if (token != null && jwtService.validateToken(token)) {
                        UsernamePasswordAuthenticationToken auth = tokenProvider.getAuthentication(token);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        accessor.setUser(auth);
                    } else {
                        throw new RuntimeException("Unauthorized");
                    }
                }
                return message;
            }
        });
    }
}
