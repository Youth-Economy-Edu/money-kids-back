package com.moneykidsback.controller;

import com.moneykidsback.model.entity.User;
import com.moneykidsback.service.UserService;
import com.moneykidsback.service.TradeService;
import com.moneykidsback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final TradeService tradeService;

    // 사용자 정보 조회 (포인트 포함)
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable String userId) {
        try {
            User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            
            // User 객체를 직접 반환하도록 수정
            return ResponseEntity.ok(user);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("data", null);
            errorResponse.put("msg", "사용자 조회 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // 사용자 포인트만 조회
    @GetMapping("/{userId}/points")
    public ResponseEntity<?> getUserPoints(@PathVariable String userId) {
        try {
            User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            
            Map<String, Object> pointsData = new HashMap<>();
            pointsData.put("userId", user.getId());
            pointsData.put("points", user.getPoints());
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("data", pointsData);
            response.put("msg", "포인트 조회 성공");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("data", null);
            errorResponse.put("msg", "포인트 조회 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // 사용자 포인트 업데이트 (개발/테스트용)
    @PutMapping("/{userId}/points")
    public ResponseEntity<?> updateUserPoints(@PathVariable String userId, @RequestBody Map<String, Object> request) {
        try {
            User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            
            Integer newPoints = (Integer) request.get("points");
            if (newPoints == null) {
                throw new RuntimeException("포인트 값이 필요합니다.");
            }
            
            user.setPoints(newPoints);
            userRepository.save(user);
            
            Map<String, Object> pointsData = new HashMap<>();
            pointsData.put("userId", user.getId());
            pointsData.put("points", user.getPoints());
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("data", pointsData);
            response.put("msg", "포인트 업데이트 성공");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("data", null);
            errorResponse.put("msg", "포인트 업데이트 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // 사용자 포트폴리오 조회
    @GetMapping("/{userId}/portfolio")
    public ResponseEntity<?> getUserPortfolio(@PathVariable String userId) {
        try {
            User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            
            // TradeService를 통해 포트폴리오 정보 가져오기
            Map<String, Object> portfolioData = tradeService.getPortfolio(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("data", portfolioData);
            response.put("msg", "포트폴리오 조회 성공");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("data", null);
            errorResponse.put("msg", "포트폴리오 조회 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
} 