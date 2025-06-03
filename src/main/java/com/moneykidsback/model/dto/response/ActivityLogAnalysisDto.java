package com.moneykidsback.model.dto.response;
// 분석을 위해 로그 데이터를 요약한 형태로 전달하는 DTO (Data Transfer Object)
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ActivityLogAnalysisDto {
    private String activityType;
    private String quizLevel;
    private String quizCategory;
    private String stockCompanySize;
    private String stockCategory;
    private String worksheetDifficulty;
    private String worksheetCategory;
    private String status;
    private LocalDateTime createdAt;
}
