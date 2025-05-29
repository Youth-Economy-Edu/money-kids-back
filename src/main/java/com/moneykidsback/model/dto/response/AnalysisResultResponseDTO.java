package com.moneykidsback.model.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 사용자 행동 분석 결과를 나타내는 DTO
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
        private String feedback;
    }
}
