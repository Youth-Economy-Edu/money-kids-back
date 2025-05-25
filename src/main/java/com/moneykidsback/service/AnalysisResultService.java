package com.moneykidsback.service;

import com.moneykidsback.model.entity.TendencyAnalysis;
import com.moneykidsback.repository.TendencyAnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

// 사용자의 최신 성향 분석 결과를 가져오는 기능을 제공
@Service
@RequiredArgsConstructor
public class AnalysisResultService {
    private final TendencyAnalysisRepository tendencyAnalysisRepository;

    public Optional<TendencyAnalysis> getLatestAnalysisResult(Long userId) {
        return tendencyAnalysisRepository.findTopByUserIdOrderByCreatedAtDesc(userId);
    }

    public Optional<TendencyAnalysis> getLatestResult(Long userId) {
        return tendencyAnalysisRepository.findById(userId);
    }
}
