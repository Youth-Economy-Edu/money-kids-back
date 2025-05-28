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

        // Map<String, Double> 구성
        Map<String, Double> scores = new LinkedHashMap<>();
        scores.put("공격투자형", result.getAggressiveScore());
        scores.put("적극투자형", result.getActiveScore());
        scores.put("위험중립형", result.getNeutralScore());
        scores.put("안정추구형", result.getStableSeekingScore());
        scores.put("안정형", result.getStableScore());

        return AnalysisResultResponseDTO.builder()
                .userId(result.getUserId())
                .createdAt(result.getCreatedAt())
                .analysisResult(AnalysisResultResponseDTO.AnalysisResult.builder()
                        .scores(scores)
                        .finalType(result.getType())
                        .finalScore(result.getScore())
                        .feedback(result.getFeedback())
                        .build())
                .build();
    }

}
