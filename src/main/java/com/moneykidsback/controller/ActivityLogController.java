package com.moneykidsback.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moneykidsback.model.dto.response.ActivityLogAnalysisDto;
import com.moneykidsback.model.entity.ActivityLog;
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

    // [1] 최근 30개 로그 조회 (LLM 분석용)
    @GetMapping("/recent")
    public List<ActivityLogAnalysisDto> getRecentLogsForAnalysis(@RequestParam String user_id) {
        return activityLogService.getRecentLogsForAnalysis(user_id);
    }

    // [2] 페이징 가능한 전체 활동 로그 조회 (프론트 리스트용)
    @GetMapping
    public Page<ActivityLog> getLogs(
            @RequestParam String user_id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return activityLogService.getLogs(user_id, page, size);
    }
}
