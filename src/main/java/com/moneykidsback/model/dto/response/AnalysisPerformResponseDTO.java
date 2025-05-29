package com.moneykidsback.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

// 행동 분석 결과 DTO
@Data
@AllArgsConstructor
public class AnalysisPerformResponseDTO {
    private String type; // 행동 유형
    private String feedback; // 행동 피드백

    // 성격 특성 점수들
    private double aggressiveness;    // 공격성
    private double assertiveness;     // 자기 주장/적극성
    private double riskNeutrality;    // 위험 중립성
    private double securityOriented;  // 안정 추구성
    private double calmness;          // 신중함
}
