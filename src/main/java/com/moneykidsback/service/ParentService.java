package com.moneykidsback.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.moneykidsback.model.entity.ActivityLog;
import com.moneykidsback.model.entity.TendencyAnalysis;
import com.moneykidsback.model.entity.User;
import com.moneykidsback.model.entity.UserQuiz;
import com.moneykidsback.model.entity.UserStock;
import com.moneykidsback.repository.ActivityLogRepository;
import com.moneykidsback.repository.StockRepository;
import com.moneykidsback.repository.TendencyAnalysisRepository;
import com.moneykidsback.repository.UserQuizRepository;
import com.moneykidsback.repository.UserRepository;
import com.moneykidsback.repository.UserStockRepository;

import lombok.RequiredArgsConstructor;

/**
 * 👨‍👩‍👧‍👦 학부모 전용 서비스
 * - 자녀의 경제 교육 현황 분석
 * - 성향 변화 추이 모니터링
 * - 학습 성과 및 투자 분석
 * - 맞춤형 교육 가이드 제공
 */
@Service
@RequiredArgsConstructor
public class ParentService {

    private final UserRepository userRepository;
    private final TendencyAnalysisRepository tendencyAnalysisRepository;
    private final ActivityLogRepository activityLogRepository;
    private final UserQuizRepository userQuizRepository;
    private final UserStockRepository userStockRepository;
    private final StockRepository stockRepository;

    /**
     * 자녀 기본 프로필 조회
     */
    public Map<String, Object> getChildProfile(String childId) {
        User child = userRepository.findById(childId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("id", child.getId());
        profile.put("name", child.getName());
        profile.put("points", child.getPoints());
        profile.put("tendency", child.getTendency());
        
        // 가입일로부터 경과 일수
        // long daysSinceJoin = child.getCreatedAt() != null ? 
        //     ChronoUnit.DAYS.between(child.getCreatedAt().toLocalDate(), LocalDate.now()) : 0;
        // profile.put("daysSinceJoin", daysSinceJoin);
        
        // 현재 레벨 정보 (포인트 기준)
        int level = calculateLevel(child.getPoints());
        profile.put("level", level);
        profile.put("nextLevelPoints", (level + 1) * 1000); // 다음 레벨까지 필요 포인트
        
        return profile;
    }

    /**
     * 자녀 경제 성향 그래프 데이터
     */
    public Map<String, Object> getChildTendencyGraph(String childId) {
        TendencyAnalysis latestAnalysis = tendencyAnalysisRepository
            .findTopByUserIdOrderByCreatedAtDesc(childId)
            .orElseThrow(() -> new RuntimeException("성향 분석 데이터를 찾을 수 없습니다."));

        Map<String, Object> graphData = new LinkedHashMap<>();
        
        // 5가지 성향 점수
        Map<String, Double> scores = new LinkedHashMap<>();
        scores.put("공격성", latestAnalysis.getAggressiveness());
        scores.put("적극성", latestAnalysis.getAssertiveness());
        scores.put("위험중립성", latestAnalysis.getRiskNeutrality());
        scores.put("안정추구성", latestAnalysis.getSecurityOriented());
        scores.put("신중함", latestAnalysis.getCalmness());
        
        graphData.put("scores", scores);
        graphData.put("finalType", latestAnalysis.getType());
        graphData.put("feedback", latestAnalysis.getFeedback());
        graphData.put("guidance", latestAnalysis.getGuidance());
        graphData.put("lastAnalyzedAt", latestAnalysis.getCreatedAt());
        
        return graphData;
    }

    /**
     * 자녀 활동 로그 요약 (최근 N일)
     */
    public Map<String, Object> getChildActivitySummary(String childId, int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<ActivityLog> recentLogs = activityLogRepository
            .findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(childId, startDate);

        Map<String, Object> summary = new LinkedHashMap<>();
        
        // 활동 유형별 카운트
        Map<String, Long> activityCounts = recentLogs.stream()
            .collect(Collectors.groupingBy(
                ActivityLog::getActivityType, 
                Collectors.counting()
            ));
        
        // 상태별 카운트
        Map<String, Long> statusCounts = recentLogs.stream()
            .collect(Collectors.groupingBy(
                ActivityLog::getStatus, 
                Collectors.counting()
            ));
        
        summary.put("totalActivities", recentLogs.size());
        summary.put("activityByType", activityCounts);
        summary.put("activityByStatus", statusCounts);
        summary.put("periodDays", days);
        summary.put("mostActiveDay", getMostActiveDay(recentLogs));
        summary.put("averageActivitiesPerDay", (double) recentLogs.size() / days);
        
        return summary;
    }

    /**
     * 자녀 학습 성과 분석
     */
    public Map<String, Object> getChildLearningProgress(String childId) {
        User child = userRepository.findById(childId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        List<UserQuiz> quizResults = userQuizRepository.findByUser(child);
        
        Map<String, Object> progress = new LinkedHashMap<>();
        
        // 퀴즈 성과 분석
        int totalQuizzes = quizResults.size();
        long correctAnswers = quizResults.stream()
            .mapToInt(quiz -> quiz.getCorrect() ? 1 : 0)
            .sum();
        
        double accuracyRate = totalQuizzes > 0 ? (double) correctAnswers / totalQuizzes * 100 : 0;
        
        progress.put("totalQuizzes", totalQuizzes);
        progress.put("correctAnswers", correctAnswers);
        progress.put("accuracyRate", Math.round(accuracyRate * 10.0) / 10.0);
        progress.put("totalPointsEarned", child.getPoints());
        progress.put("currentLevel", calculateLevel(child.getPoints()));
        
        // 최근 학습 트렌드 (정답률 변화)
        List<Map<String, Object>> recentTrend = getRecentQuizTrend(quizResults);
        progress.put("recentTrend", recentTrend);
        
        return progress;
    }

    /**
     * 자녀 투자 포트폴리오 분석 (학부모용)
     */
    public Map<String, Object> getChildInvestmentAnalysis(String childId) {
        User child = userRepository.findById(childId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        List<UserStock> userStocks = userStockRepository.findByUser(child);
        
        Map<String, Object> analysis = new LinkedHashMap<>();
        
        if (userStocks.isEmpty()) {
            analysis.put("hasInvestments", false);
            analysis.put("message", "아직 투자 활동이 없습니다.");
            return analysis;
        }
        
        analysis.put("hasInvestments", true);
        analysis.put("totalStocks", userStocks.size());
        
        // 포트폴리오 구성
        Map<String, Integer> stockComposition = userStocks.stream()
            .collect(Collectors.toMap(
                us -> us.getStock().getName(),
                UserStock::getQuantity
            ));
        
        // 카테고리별 분산
        Map<String, Long> categoryDistribution = userStocks.stream()
            .collect(Collectors.groupingBy(
                us -> us.getStock().getCategory(),
                Collectors.counting()
            ));
        
        // 총 투자 가치 계산
        int totalInvestmentValue = userStocks.stream()
            .mapToInt(UserStock::getTotal)
            .sum();
        
        analysis.put("stockComposition", stockComposition);
        analysis.put("categoryDistribution", categoryDistribution);
        analysis.put("totalInvestmentValue", totalInvestmentValue);
        analysis.put("diversificationScore", calculateDiversificationScore(categoryDistribution));
        
        return analysis;
    }

    /**
     * 학부모용 통합 대시보드 데이터
     */
    public Map<String, Object> getParentDashboard(String childId) {
        Map<String, Object> dashboard = new LinkedHashMap<>();
        
        try {
            // 기본 프로필
            dashboard.put("profile", getChildProfile(childId));
            
            // 최신 성향 분석 (간단 버전)
            try {
                Map<String, Object> tendency = getChildTendencyGraph(childId);
                dashboard.put("tendency", Map.of(
                    "type", tendency.get("finalType"),
                    "lastAnalyzed", tendency.get("lastAnalyzedAt")
                ));
            } catch (Exception e) {
                dashboard.put("tendency", Map.of("type", "분석 필요", "lastAnalyzed", null));
            }
            
            // 최근 7일 활동 요약
            dashboard.put("recentActivity", getChildActivitySummary(childId, 7));
            
            // 학습 성과
            dashboard.put("learningProgress", getChildLearningProgress(childId));
            
            // 투자 현황 (간단 버전)
            Map<String, Object> investment = getChildInvestmentAnalysis(childId);
            dashboard.put("investment", Map.of(
                "hasInvestments", investment.get("hasInvestments"),
                "totalStocks", investment.getOrDefault("totalStocks", 0),
                "totalValue", investment.getOrDefault("totalInvestmentValue", 0)
            ));
            
        } catch (Exception e) {
            dashboard.put("error", "대시보드 데이터 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return dashboard;
    }

    /**
     * 자녀 성향 변화 추이 분석
     */
    public List<Map<String, Object>> getChildTendencyHistory(String childId) {
        List<TendencyAnalysis> history = tendencyAnalysisRepository
            .findByUserIdOrderByCreatedAtDesc(childId);
        
        return history.stream()
            .map(analysis -> {
                Map<String, Object> record = new LinkedHashMap<>();
                record.put("date", analysis.getCreatedAt().toLocalDate());
                record.put("type", analysis.getType());
                record.put("scores", Map.of(
                    "공격성", analysis.getAggressiveness(),
                    "적극성", analysis.getAssertiveness(),
                    "위험중립성", analysis.getRiskNeutrality(),
                    "안정추구성", analysis.getSecurityOriented(),
                    "신중함", analysis.getCalmness()
                ));
                record.put("feedback", analysis.getFeedback());
                return record;
            })
            .collect(Collectors.toList());
    }

    /**
     * 자녀 경제 교육 추천사항
     */
    public Map<String, Object> getChildRecommendations(String childId) {
        Map<String, Object> recommendations = new LinkedHashMap<>();
        
        try {
            // 성향 기반 추천
            TendencyAnalysis latestAnalysis = tendencyAnalysisRepository
                .findTopByUserIdOrderByCreatedAtDesc(childId)
                .orElse(null);
            
            if (latestAnalysis != null) {
                recommendations.put("tendencyBasedAdvice", latestAnalysis.getGuidance());
                recommendations.put("recommendedLearningAreas", 
                    getRecommendedLearningAreas(latestAnalysis));
            }
            
            // 학습 성과 기반 추천
            Map<String, Object> progress = getChildLearningProgress(childId);
            double accuracyRate = (Double) progress.get("accuracyRate");
            
            List<String> learningRecommendations = new ArrayList<>();
            if (accuracyRate < 60) {
                learningRecommendations.add("기초 경제 개념 복습이 필요합니다.");
                learningRecommendations.add("쉬운 난이도의 퀴즈부터 시작해보세요.");
            } else if (accuracyRate < 80) {
                learningRecommendations.add("중급 수준의 경제 학습을 권장합니다.");
                learningRecommendations.add("투자 시뮬레이션을 더 활용해보세요.");
            } else {
                learningRecommendations.add("고급 경제 개념 학습을 시도해보세요.");
                learningRecommendations.add("복합적인 투자 전략을 연습해보세요.");
            }
            
            recommendations.put("learningRecommendations", learningRecommendations);
            
            // 투자 관련 추천
            Map<String, Object> investment = getChildInvestmentAnalysis(childId);
            List<String> investmentRecommendations = new ArrayList<>();
            
            if (!(Boolean) investment.get("hasInvestments")) {
                investmentRecommendations.add("가상 투자를 시작해보세요.");
                investmentRecommendations.add("안전한 주식부터 투자해보는 것을 권장합니다.");
            } else {
                int diversificationScore = (Integer) investment.get("diversificationScore");
                if (diversificationScore < 3) {
                    investmentRecommendations.add("다양한 업종의 주식에 분산 투자해보세요.");
                }
                investmentRecommendations.add("투자 결과를 정기적으로 검토해보세요.");
            }
            
            recommendations.put("investmentRecommendations", investmentRecommendations);
            
        } catch (Exception e) {
            recommendations.put("error", "추천사항 생성 중 오류가 발생했습니다.");
        }
        
        return recommendations;
    }

    // === 유틸리티 메서드들 ===

    private int calculateLevel(int points) {
        return Math.max(1, points / 1000 + 1);
    }

    private String getMostActiveDay(List<ActivityLog> logs) {
        if (logs.isEmpty()) return "데이터 없음";
        
        Map<String, Long> dayCount = logs.stream()
            .collect(Collectors.groupingBy(
                log -> log.getCreatedAt().getDayOfWeek().toString(),
                Collectors.counting()
            ));
        
        return dayCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("데이터 없음");
    }

    private List<Map<String, Object>> getRecentQuizTrend(List<UserQuiz> quizResults) {
        // 최근 10개 퀴즈의 정답률 트렌드
        List<Map<String, Object>> trend = new ArrayList<>();
        
        int batchSize = 5; // 5개씩 묶어서 분석
        for (int i = 0; i < quizResults.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, quizResults.size());
            List<UserQuiz> batch = quizResults.subList(i, endIndex);
            
            long correctCount = batch.stream()
                .mapToInt(quiz -> quiz.getCorrect() ? 1 : 0)
                .sum();
            
            double batchAccuracy = (double) correctCount / batch.size() * 100;
            
            Map<String, Object> point = new HashMap<>();
            point.put("batch", (i / batchSize) + 1);
            point.put("accuracy", Math.round(batchAccuracy * 10.0) / 10.0);
            trend.add(point);
            
            if (trend.size() >= 5) break; // 최대 5개 포인트
        }
        
        return trend;
    }

    private int calculateDiversificationScore(Map<String, Long> categoryDistribution) {
        return categoryDistribution.size(); // 단순히 카테고리 수로 계산
    }

    private List<String> getRecommendedLearningAreas(TendencyAnalysis analysis) {
        List<String> areas = new ArrayList<>();
        
        // 성향별 추천 학습 영역
        if (analysis.getAggressiveness() > 70) {
            areas.add("리스크 관리 학습");
            areas.add("안정적 투자 전략");
        }
        
        if (analysis.getSecurityOriented() > 70) {
            areas.add("적극적 투자 기법");
            areas.add("성장 투자 전략");
        }
        
        if (analysis.getCalmness() > 70) {
            areas.add("빠른 의사결정 연습");
            areas.add("시장 타이밍 학습");
        }
        
        if (areas.isEmpty()) {
            areas.add("균형잡힌 투자 포트폴리오");
            areas.add("기본 경제 원리 복습");
        }
        
        return areas;
    }
} 