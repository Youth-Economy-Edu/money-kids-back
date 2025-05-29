package com.moneykidsback.service;

import com.moneykidsback.model.dto.response.AnalysisResultResponseDTO;
import com.moneykidsback.model.entity.TendencyAnalysis;
import com.moneykidsback.repository.TendencyAnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

// 사용자의 최신 성향 분석 결과를 가져오는 기능을 제공
@Service
@RequiredArgsConstructor
public class AnalysisResultService {
    private final TendencyAnalysisRepository tendencyAnalysisRepository;

    public AnalysisResultResponseDTO getLatestResult(String userId) {
        TendencyAnalysis result = tendencyAnalysisRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자 없음"));

        // 영문 성향 점수만 전달
        Map<String, Double> traits = new LinkedHashMap<>();
        traits.put("aggressiveness", result.getAggressiveness());
        traits.put("assertiveness", result.getAssertiveness());
        traits.put("risk_neutrality", result.getRiskNeutrality());
        traits.put("security_oriented", result.getSecurityOriented());
        traits.put("calmness", result.getCalmness());

        return AnalysisResultResponseDTO.builder()
                .userId(result.getUserId())
                .createdAt(result.getCreatedAt())
                .analysisResult(AnalysisResultResponseDTO.AnalysisResult.builder()
                        .scores(traits)
                        .finalType(result.getType())
                        .feedback(result.getFeedback())
                        .build())
                .build();
    }
}