package com.moneykidsback.controller;

import com.moneykidsback.model.entity.User;
import com.moneykidsback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 사용자 정보 조회 (포인트 포함)
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable String userId) {
        try {
            User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", Map.of(
                    "id", user.getId(),
                    "name", user.getName(),
                    "points", user.getPoints(),
                    "tendency", user.getTendency()
                ),
                "msg", "사용자 정보 조회 성공"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "code", 500,
                "data", null,
                "msg", "사용자 조회 실패: " + e.getMessage()
            ));
        }
    }

    // 사용자 포인트만 조회
    @GetMapping("/{userId}/points")
    public ResponseEntity<?> getUserPoints(@PathVariable String userId) {
        try {
            User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", Map.of(
                    "userId", user.getId(),
                    "points", user.getPoints()
                ),
                "msg", "포인트 조회 성공"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "code", 500,
                "data", null,
                "msg", "포인트 조회 실패: " + e.getMessage()
            ));
        }
    }
} 