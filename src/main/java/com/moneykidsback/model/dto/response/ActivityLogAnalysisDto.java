package com.moneykidsback.model.dto.response;

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
