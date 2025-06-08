package com.moneykidsback.service;

import com.moneykidsback.model.dto.response.ActivityLogAnalysisDto;
import com.moneykidsback.model.entity.ActivityLog;
import com.moneykidsback.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public void logQuiz(String userId, String level, String category, String status) {
        ActivityLog log = ActivityLog.builder()
                .userId(userId)
                .activityType("QUIZ")
                .quizLevel(level)
                .quizCategory(category)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();
        activityLogRepository.save(log);
    }

    public void logStock(String userId, String companySize, String category, String status) {
        ActivityLog log = ActivityLog.builder()
                .userId(userId)
                .activityType("STOCK_TRADE")
                .stockCompanySize(companySize)
                .stockCategory(category)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();
        activityLogRepository.save(log);
    }

    public void logCompletion(String userId, String difficulty, String category, String status) {
        ActivityLog log = ActivityLog.builder()
                .userId(userId)
                .activityType("CONTENT_COMPLETION")
                .worksheetDifficulty(difficulty)
                .worksheetCategory(category)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();
        activityLogRepository.save(log);
    }

    // 최근 30개 로그 조회 (분석용)
    public List<ActivityLogAnalysisDto> getRecentLogsForAnalysis(String userId) {
        return activityLogRepository.findTop30ByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(log -> new ActivityLogAnalysisDto(
                        log.getActivityType(),
                        log.getQuizLevel(),
                        log.getQuizCategory(),
                        log.getStockCompanySize(),
                        log.getStockCategory(),
                        log.getWorksheetDifficulty(),
                        log.getWorksheetCategory(),
                        log.getStatus(),
                        log.getCreatedAt()
                )).collect(Collectors.toList());
    }

    // 페이지 기반 전체 로그 조회
    public Page<ActivityLog> getLogs(String userId, int page, int size) {
        return activityLogRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size));
    }
}
