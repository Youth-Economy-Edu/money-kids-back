package com.moneykidsback.controller;
// 학생 활동 로그를 분석용으로 조회할 수 있는 관리자용 컨트롤러
import com.moneykidsback.model.dto.response.ActivityLogAnalysisDto;
import com.moneykidsback.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/logs")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;
    // [GET] 특정 학생의 최근 활동 로그 30개 반환 (분석용)
    @GetMapping
    public ResponseEntity<List<ActivityLogAnalysisDto>> getLogsForAnalysis(@RequestParam("user_id") String userId) {
        List<ActivityLogAnalysisDto> logs = activityLogService.getRecentLogsForAnalysis(userId);
        return ResponseEntity.ok(logs);
    }
}

