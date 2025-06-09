package com.moneykidsback.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moneykidsback.service.UserService;

import lombok.RequiredArgsConstructor;

/**
 * 🔐 사용자 인증 컨트롤러
 * - 일반 회원가입/로그인
 * - OAuth2 소셜 로그인 (Google, Kakao) 지원
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestParam String id,
                                        @RequestParam String name,
                                        @RequestParam String password) {
        try {
            userService.registerUser(id, name, password);
            return ResponseEntity.ok().body(Map.of("message", "회원가입이 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String id,
                                 @RequestParam String password) {
        try {
            userService.login(id, password);
            return ResponseEntity.ok().body(Map.of("message", "로그인이 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}