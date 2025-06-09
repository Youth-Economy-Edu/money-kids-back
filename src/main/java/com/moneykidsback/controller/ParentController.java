package com.moneykidsback.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moneykidsback.service.ParentService;

import lombok.RequiredArgsConstructor;

/**
 * 👨‍👩‍👧‍👦 학부모 전용 대시보드 컨트롤러
 * - 자녀 프로필 및 학습 현황 조회
 * - 경제 성향 분석 그래프 데이터
 * - 자녀 활동 로그 및 성과 분석
 * - 학부모용 통합 대시보드
 */
@RestController
@RequestMapping("/api/parent")
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;

    /**
     * 자녀 기본 프로필 조회
     */
    @GetMapping("/child/{childId}/profile")
    public ResponseEntity<?> getChildProfile(@PathVariable String childId) {
        try {
            Map<String, Object> profile = parentService.getChildProfile(childId);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", profile,
                "msg", "자녀 프로필 조회 성공"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "code", 500,
                "data", null,
                "msg", "프로필 조회 실패: " + e.getMessage()
            ));
        }
    }

    /**
     * 자녀 경제 성향 그래프 데이터
     */
    @GetMapping("/child/{childId}/tendency-graph")
    public ResponseEntity<?> getChildTendencyGraph(@PathVariable String childId) {
        try {
            Map<String, Object> tendencyData = parentService.getChildTendencyGraph(childId);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", tendencyData,
                "msg", "성향 그래프 데이터 조회 성공"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "code", 500,
                "data", null,
                "msg", "성향 데이터 조회 실패: " + e.getMessage()
            ));
        }
    }

    /**
     * 자녀 활동 로그 요약 (학부모용)
     */
    @GetMapping("/child/{childId}/activity-summary")
    public ResponseEntity<?> getChildActivitySummary(
            @PathVariable String childId,
            @RequestParam(defaultValue = "7") int days) {
        try {
            Map<String, Object> summary = parentService.getChildActivitySummary(childId, days);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", summary,
                "msg", "활동 요약 조회 성공"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "code", 500,
                "data", null,
                "msg", "활동 요약 조회 실패: " + e.getMessage()
            ));
        }
    }

    /**
     * 자녀 학습 성과 분석
     */
    @GetMapping("/child/{childId}/learning-progress")
    public ResponseEntity<?> getChildLearningProgress(@PathVariable String childId) {
        try {
            Map<String, Object> progress = parentService.getChildLearningProgress(childId);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", progress,
                "msg", "학습 성과 조회 성공"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "code", 500,
                "data", null,
                "msg", "학습 성과 조회 실패: " + e.getMessage()
            ));
        }
    }

    /**
     * 자녀 투자 포트폴리오 분석 (학부모용)
     */
    @GetMapping("/child/{childId}/investment-analysis")
    public ResponseEntity<?> getChildInvestmentAnalysis(@PathVariable String childId) {
        try {
            Map<String, Object> analysis = parentService.getChildInvestmentAnalysis(childId);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", analysis,
                "msg", "투자 분석 조회 성공"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "code", 500,
                "data", null,
                "msg", "투자 분석 조회 실패: " + e.getMessage()
            ));
        }
    }

    /**
     * 학부모용 통합 대시보드 데이터
     */
    @GetMapping("/child/{childId}/dashboard")
    public ResponseEntity<?> getParentDashboard(@PathVariable String childId) {
        try {
            Map<String, Object> dashboard = parentService.getParentDashboard(childId);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", dashboard,
                "msg", "대시보드 데이터 조회 성공"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "code", 500,
                "data", null,
                "msg", "대시보드 데이터 조회 실패: " + e.getMessage()
            ));
        }
    }

    /**
     * 자녀 성향 변화 추이 분석
     */
    @GetMapping("/child/{childId}/tendency-history")
    public ResponseEntity<?> getChildTendencyHistory(@PathVariable String childId) {
        try {
            List<Map<String, Object>> history = parentService.getChildTendencyHistory(childId);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", history,
                "msg", "성향 변화 추이 조회 성공"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "code", 500,
                "data", null,
                "msg", "성향 변화 추이 조회 실패: " + e.getMessage()
            ));
        }
    }

    /**
     * 자녀 경제 교육 추천사항
     */
    @GetMapping("/child/{childId}/recommendations")
    public ResponseEntity<?> getChildRecommendations(@PathVariable String childId) {
        try {
            Map<String, Object> recommendations = parentService.getChildRecommendations(childId);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", recommendations,
                "msg", "교육 추천사항 조회 성공"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "code", 500,
                "data", null,
                "msg", "추천사항 조회 실패: " + e.getMessage()
            ));
        }
    }
} 