package com.moneykidsback.service;
// 학생 활동 로그를 저장하고 조회하는 서비스 클래스
import com.moneykidsback.model.dto.response.ActivityLogAnalysisDto;
import com.moneykidsback.model.entity.ActivityLog;
import com.moneykidsback.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    // [1] 퀴즈 활동 로그 저장
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
    // [2] 주식 활동 로그 저장
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
    // [3] 학습지 활동 로그 저장
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
    // [4] 특정 학생의 최근 활동 로그 30개 조회 (LLM 분석용)
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

}
