package com.moneykidsback.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;


@Configuration
@EnableWebSocketMessageBroker //웹소켓 메세지 브로커 클래스 선언
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // 1. 메세지 브로커 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/updates", "/alerts");  // 서버 -> 클라이언트 방향 prefix(채널)
        registry.setApplicationDestinationPrefixes("/send/stock-info"); // 클라이언트 -> 서버 방향 prefix
    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS(); //웹소켓 엔드포인트
    }
}
