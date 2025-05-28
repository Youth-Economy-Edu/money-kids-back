package com.moneykidsback.model.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisResultResponseDTO {

    private String userId;
    private AnalysisResult analysisResult;
    private LocalDateTime createdAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnalysisResult {
        private Map<String, Double> scores;
        private String finalType;
        private double finalScore;
        private String feedback;
    }
}
