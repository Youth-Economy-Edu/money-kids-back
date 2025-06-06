package com.moneykidsback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                // 정적 리소스(CSS, JS, 이미지 등) 경로 허용
                                "/css/**", "/js/**", "/images/**", "/favicon.ico",
                                
                                // 페이지 및 API 경로 허용
                                "/", "/login", "/register", "/home",
                                "/api/auth/**",
                                "/oauth2/**",
                                "/login/oauth2/code/**",
                                "/error"
                        ).permitAll() // 위에 명시된 경로는 모두 접근 허용
                        
                        // 나머지 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                );
        
        return http.build();
    }
}