package com.moneykidsback.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moneykidsback.model.dto.response.ActivityLogAnalysisDto;
import com.moneykidsback.model.entity.ActivityLog;
import com.moneykidsback.repository.ActivityLogRepository;
import com.moneykidsback.service.ActivityLogService;

import lombok.RequiredArgsConstructor;

/**
 * 📊 사용자 활동 로그 컨트롤러
 * - 사용자 행동 데이터 수집
 * - 학부모용 자녀 활동 조회
 * - 성향 분석을 위한 데이터 제공
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;
    private final ActivityLogRepository activityLogRepository;

    // [1] 최근 30개 로그 조회 (LLM 분석용)
    @GetMapping("/analysis")
    public List<ActivityLogAnalysisDto> getActivityLogsForAnalysis(@RequestParam String user_id) {
        return activityLogService.getRecentLogsForAnalysis(user_id);
    }

    // [2] 사용자별 활동 로그 조회 (페이징)
    @GetMapping("/logs")
    public Page<ActivityLog> getLogs(
            @RequestParam String user_id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return activityLogService.getLogs(user_id, page, size);
    }

    /**
     * 테스트용 ActivityLog 생성
     */
    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> createTestActivityLog(
            @RequestParam String userId,
            @RequestParam String activityType,
            @RequestParam(required = false) String status) {
        
        ActivityLog activityLog = ActivityLog.builder()
            .userId(userId)
            .activityType(activityType)
            .status(status != null ? status : "SUCCESS")
            .createdAt(LocalDateTime.now())
            .build();
        
        ActivityLog saved = activityLogRepository.save(activityLog);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("msg", "ActivityLog 생성 성공");
        response.put("data", saved);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 사용자의 ActivityLog 조회
     */
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserActivityLogs(
            @RequestParam String userId) {
        
        List<ActivityLog> logs = activityLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("msg", "ActivityLog 조회 성공");
        response.put("data", logs);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 모든 ActivityLog 조회
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllActivityLogs() {
        
        List<ActivityLog> logs = activityLogRepository.findAllByOrderByCreatedAtDesc();
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("msg", "모든 ActivityLog 조회 성공");
        response.put("data", logs);
        
        return ResponseEntity.ok(response);
    }
}
