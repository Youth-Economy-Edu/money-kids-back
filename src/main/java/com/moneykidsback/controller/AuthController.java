package com.moneykidsback.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moneykidsback.model.entity.User;
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
            User newUser = userService.registerUser(id, name, password);
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", newUser.getId());
            userData.put("name", newUser.getName() != null ? newUser.getName() : "");
            userData.put("points", newUser.getPoints());
            userData.put("tendency", newUser.getTendency() != null ? newUser.getTendency() : "");
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("user", userData);
            response.put("message", "회원가입이 완료되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("success", false);
            errorResponse.put("error", "서버 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String id,
                                 @RequestParam String password) {
        try {
            User user = userService.login(id, password);
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("name", user.getName() != null ? user.getName() : "");
            userData.put("points", user.getPoints());
            userData.put("tendency", user.getTendency() != null ? user.getTendency() : "");
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("success", true);
            response.put("user", userData);
            response.put("message", "로그인이 완료되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("success", false);
            errorResponse.put("error", "서버 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}