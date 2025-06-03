package com.moneykidsback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/**",  // 로그인 관련 경로
                                "/error",               // 오류 처리
                                "/",                    // 루트 페이지
                                "/home/**",                // 홈 (로그아웃 후 접근 허용 시)
                                "/api/auth/register"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout
                        .logoutUrl("/api/users/logout")
                        .logoutSuccessUrl("/api/users/login") // 로그아웃 후 이동할 경로
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )

                .csrf(csrf -> csrf.disable()); // 개발 중 CSRF 끄기

        return http.build();
    }
}