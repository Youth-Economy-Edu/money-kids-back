package com.moneykidsback.controller;

import com.moneykidsback.model.dto.response.ActivityLogAnalysisDto;
import com.moneykidsback.model.entity.ActivityLog;
import com.moneykidsback.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
