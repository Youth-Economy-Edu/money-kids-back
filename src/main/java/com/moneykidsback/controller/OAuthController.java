package com.moneykidsback.controller;

import com.moneykidsback.model.entity.User;
import com.moneykidsback.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * OAuth 로그인 컨트롤러
 * Kakao, Google 소셜 로그인 처리
 */
@Slf4j
@RestController
@RequestMapping("/api/users/login")
@RequiredArgsConstructor
public class OAuthController {
    
    private final OAuthService oAuthService;
    
    /**
     * 카카오 로그인 콜백 처리
     */
    @GetMapping("/kakao/callback")
    public ResponseEntity<?> kakaoCallback(
            @RequestParam("code") String code,
            HttpServletResponse response) {
        try {
            log.info("카카오 로그인 콜백 - code: {}", code);
            
            // 카카오 인증 처리
            User user = oAuthService.processKakaoLogin(code);
            
            // 로그인 성공 응답
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("name", user.getName());
            userData.put("email", user.getEmail());
            userData.put("points", user.getPoints());
            
            // 프론트엔드로 리다이렉트 (로그인 성공)
            String redirectUrl = String.format(
                "http://localhost:5175/login-success?userId=%s&userName=%s",
                user.getId(), user.getName()
            );
            
            response.sendRedirect(redirectUrl);
            return null;
            
        } catch (Exception e) {
            log.error("카카오 로그인 실패", e);
            try {
                response.sendRedirect("http://localhost:5175/login?error=kakao_login_failed");
            } catch (IOException ex) {
                log.error("리다이렉트 실패", ex);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "카카오 로그인 처리 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 구글 로그인 콜백 처리
     */
    @GetMapping("/google/callback")
    public ResponseEntity<?> googleCallback(
            @RequestParam("code") String code,
            HttpServletResponse response) {
        try {
            log.info("구글 로그인 콜백 - code: {}", code);
            
            // 구글 인증 처리
            User user = oAuthService.processGoogleLogin(code);
            
            // 로그인 성공 응답
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("name", user.getName());
            userData.put("email", user.getEmail());
            userData.put("points", user.getPoints());
            
            // 프론트엔드로 리다이렉트 (로그인 성공)
            String redirectUrl = String.format(
                "http://localhost:5175/login-success?userId=%s&userName=%s",
                user.getId(), user.getName()
            );
            
            response.sendRedirect(redirectUrl);
            return null;
            
        } catch (Exception e) {
            log.error("구글 로그인 실패", e);
            try {
                response.sendRedirect("http://localhost:5175/login?error=google_login_failed");
            } catch (IOException ex) {
                log.error("리다이렉트 실패", ex);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "구글 로그인 처리 중 오류가 발생했습니다."));
        }
    }
} 