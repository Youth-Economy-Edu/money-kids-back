package com.moneykidsback.controller;

import com.moneykidsback.model.dto.request.AnalysisPerformRequestDTO;
import com.moneykidsback.model.dto.response.AnalysisPerformResponseDTO;
import com.moneykidsback.model.dto.response.AnalysisResultResponseDTO;
import com.moneykidsback.model.entity.TendencyAnalysis;
import com.moneykidsback.service.AnalysisPerformService;
import com.moneykidsback.service.AnalysisResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

// API 요청을 처리하는 컨트롤러
// 분석 실행 로직: 분석 요청을 받고 결과를 반환하는 역할을 수행
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis")
public class AnalysisPerformController {

    private final AnalysisResultService analysisResultService;
    private final AnalysisPerformService analysisPerformService;

    // 사용자의 활동 로그를 기반으로 투자 성향 분석을 수행하는 API
    @PostMapping("/perform")
    public ResponseEntity<?> performAnalysis(@RequestBody AnalysisPerformRequestDTO requestDTO) {
        if (requestDTO.getUserId() == null || requestDTO.getActivityLogs() == null || requestDTO.getActivityLogs().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    "400, user_id 또는 activity_logs가 누락되었습니다.");
        }

        try {
            TendencyAnalysis result = analysisPerformService.performAnalysis(
                    String.valueOf(requestDTO.getUserId()), requestDTO.getActivityLogs()
            );

            // finalScore 제거됨 → type, feedback만 전달
            return ResponseEntity.ok(new ResultResponse(
                    200,
                    Map.of(
                            "type", result.getType(),
                            "feedback", result.getFeedback(),
                            "guidance", result.getGuidance()
                    ),
                    "200 ok"
            ));

        } catch (Exception e) {
            e.printStackTrace(); // or log.error("분석 실패", e);
            return ResponseEntity.status(500).body(
                    new ResultResponse(500, null, "LLM 분석 중 오류가 발생했습니다.")
            );
        }
    }

    // 내부 응답 포맷
    record ResultResponse(int code, Object data, String msg) {
    }

    // 사용자의 최신 분석 결과를 가져오는 API
    @GetMapping("/result")
    public ResponseEntity<?> getLatestResult(@RequestParam("user_id") String userId) {
        try {
            AnalysisResultResponseDTO response = analysisResultService.getLatestResult(userId);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("해당 사용자의 분석 결과를 찾을 수 없습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 내부 오류");
        }
    }

    // 특정 사용자의 성향 분석 결과를 생성일 기준으로 내림차순 정렬하여 가져오는 API
    @GetMapping("/history")
    public ResponseEntity<?> getAnalysisHistory(@RequestParam("user_id") String userId) {
        List<TendencyAnalysis> history = analysisResultService.getAnalysisHistory(userId);

        List<Map<String, Object>> response = history.stream().map(record -> {
            Map<String, Double> scores = Map.of(
                    "공격성", record.getAggressiveness(),
                    "적극성", record.getAssertiveness(),
                    "위험중립성", record.getRiskNeutrality(),
                    "안정성 추구", record.getSecurityOriented(),
                    "신중함", record.getCalmness()
            );

            return Map.of(
                    "userId", record.getUserId(),
                    "analysisResult", Map.of(
                            "scores", scores,
                            "finalType", record.getType(),
                            "feedback", record.getFeedback(),
                            "guidance", record.getGuidance()
                    ),
                    "createdAt", record.getCreatedAt()
            );
        }).toList();

        return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", response,
                "msg", "200 OK"
        ));
    }

    // 특정 사용자의 성향 분석 결과 전체 삭제 API
    // 굳이 필요할 것 같지 않으나 이스터에그로 남겨둠
    @DeleteMapping("/result")
    public ResponseEntity<?> deleteAnalysis(@RequestParam("user_id") String userId) {
        try {
            analysisResultService.deleteAnalysisByUserId(userId);
            return ResponseEntity.ok(Map.of("code", 200, "msg", "200 ok"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "msg", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("code", 500, "msg", "서버 내부 오류"));
        }
    }
}
