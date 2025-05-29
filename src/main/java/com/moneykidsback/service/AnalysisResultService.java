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

    // 특정 사용자의 성향 분석 결과를 생성일 기준으로 내림차순 정렬하여 가져오기
    public List<TendencyAnalysis> getAnalysisHistory(String userId) {
        return tendencyAnalysisRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // 특정 사용자의 성향 분석 결과 전체 삭제
    public void deleteAnalysisByUserId(String userId) {
        List<TendencyAnalysis> results = tendencyAnalysisRepository.findAllByUserId(userId);

        if (results.isEmpty()) {
            throw new NoSuchElementException("해당 사용자의 성향 분석 결과가 없습니다.");
        }

        tendencyAnalysisRepository.deleteAll(results);
    }
}