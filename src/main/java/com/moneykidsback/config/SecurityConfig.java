package com.moneykidsback.config;

import com.moneykidsback.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

//  개발 중 임시로 보안 끄기 위한 클래스
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;

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
                                "/api/users/login/**",
                                "/error", "/api","/api/quizzes/submit",
                                "/api/quizzes/result"
                        ).permitAll() // 위에 명시된 경로는 모두 접근 허용

                        // 나머지 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .successHandler(authenticationSuccessHandler)
                );

        return http.build();
    }
}
