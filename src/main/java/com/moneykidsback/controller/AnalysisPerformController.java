package com.moneykidsback.controller;

import com.moneykidsback.model.dto.request.AnalysisPerformRequestDTO;
import com.moneykidsback.model.dto.response.AnalysisPerformResponseDTO;
import com.moneykidsback.model.entity.TendencyAnalysis;
import com.moneykidsback.service.AnalysisPerformService;
import com.moneykidsback.service.AnalysisResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// API 요청을 처리하는 컨트롤러
// 분석 실행 로직: 분석 요청을 받고 결과를 반환하는 역할을 수행
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis")
public class AnalysisPerformController {

    private final AnalysisResultService analysisResultService;
    private final AnalysisPerformService analysisPerformService;

    @PostMapping("/perform")
    public ResponseEntity<?> performAnalysis(@RequestBody AnalysisPerformRequestDTO requestDTO) {
        if (requestDTO.getUserId() == null || requestDTO.getActivityLogs() == null || requestDTO.getActivityLogs().isEmpty() ) {
            return ResponseEntity.badRequest().body(
                    "400, user_id 또는 activity_logs가 누락되었습니다.");
        }

        try {
            TendencyAnalysis result = analysisPerformService.performAnalysis(
                    String.valueOf(requestDTO.getUserId()), requestDTO.getActivityLogs()
            );

            return ResponseEntity.ok(new ResultResponse(200,
                    new AnalysisPerformResponseDTO(result.getType(), result.getScore(), result.getFeedback()),
                    "200 ok"));
        } catch (Exception e) {
            e.printStackTrace(); // or log.error("분석 실패", e);
            return ResponseEntity.status(500).body(
                    new ResultResponse(500, null, "LLM 분석 중 오류가 발생했습니다.")
            );
        }
    }
    // 내부 응답 포맷
    record ResultResponse(int code, Object data, String msg) {}
}
