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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 📊 사용자 활동 로그 컨트롤러
 * - 사용자 행동 데이터 수집
 * - 학부모용 자녀 활동 조회
 * - 성향 분석을 위한 데이터 제공
 */
@Tag(name = "Activity Log", description = "사용자 활동 로그 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;
    private final ActivityLogRepository activityLogRepository;

    // [1] 최근 30개 로그 조회 (LLM 분석용)
    @Operation(summary = "분석용 활동 로그 조회", description = "LLM 분석을 위한 최근 30개 활동 로그를 조회합니다")
    @GetMapping("/analysis")
    public List<ActivityLogAnalysisDto> getActivityLogsForAnalysis(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String user_id) {
        return activityLogService.getRecentLogsForAnalysis(user_id);
    }

    // [2] 사용자별 활동 로그 조회 (페이징)
    @Operation(summary = "활동 로그 목록 조회", description = "사용자별 활동 로그를 페이징하여 조회합니다")
    @GetMapping("/logs")
    public Page<ActivityLog> getLogs(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String user_id,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        return activityLogService.getLogs(user_id, page, size);
    }

    /**
     * 테스트용 ActivityLog 생성
     */
    @Operation(summary = "테스트 활동 로그 생성", description = "테스트용 활동 로그를 생성합니다")
    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> createTestActivityLog(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId,
            @Parameter(description = "활동 타입", required = true) @RequestParam String activityType,
            @Parameter(description = "상태") @RequestParam(required = false) String status) {
        
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
    @Operation(summary = "사용자 활동 로그 조회", description = "특정 사용자의 모든 활동 로그를 조회합니다")
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserActivityLogs(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId) {
        
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
    @Operation(summary = "전체 활동 로그 조회", description = "모든 사용자의 활동 로그를 조회합니다")
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
