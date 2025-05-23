package com.moneykidsback.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    // ① 클라이언트가 연결할 엔드포인트
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/stock-endpoint")        // FE가 이 URL로 SockJS 연결
                .setAllowedOrigins("*")    // CORS 허용 (접근 가능한 도메인 설정) -> 배포 후 도메인 주소로 바꾸기
                .withSockJS();             // SockJS fallback
    }

    // ② 메모리 브로커 설정 (간단한 사용)
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry
                .enableSimpleBroker("/updates", "/alerts");  // 서버가 “푸시”용으로 쓰는 채널 prefix
        registry
                .setApplicationDestinationPrefixes("/send"); // 클라이언트가 서버 메소드 호출할 때 붙이는 prefix
    }
}
