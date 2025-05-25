package com.moneykidsback.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

// 행동 분석 결과 DTO
@Data
@AllArgsConstructor
public class AnalysisPerformResponseDTO {
    private String type; // 행동 유형
    private double score; // 행동 점수
    private String feedback; // 행동 피드백
}
