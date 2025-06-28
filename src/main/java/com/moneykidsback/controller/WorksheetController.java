package com.moneykidsback.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moneykidsback.model.dto.request.WorksheetCompleteRequestDto;
import com.moneykidsback.model.dto.response.WorksheetDetailResponseDto;
import com.moneykidsback.model.dto.response.WorksheetResponseDto;
import com.moneykidsback.service.WorksheetService;

/**
 * 📚 경제 개념 학습 컨트롤러
 * - 카테고리별 경제 개념 (저축, 투자, 소비 등)
 * - 난이도별 학습 콘텐츠
 * - 워크시트 형태의 학습 자료
 */
@RestController
@RequestMapping("/api")
public class WorksheetController {

    @Autowired
    private WorksheetService worksheetService;

    // ✅ 난이도별 목록 (id + title만)
    @GetMapping("/worksheet/difficulty/{level}")
    public ResponseEntity<Map<String, Object>> getConceptIds(@PathVariable int level) {
        List<WorksheetResponseDto> ids = worksheetService.getConceptsByDifficulty(level);

        Map<String, Object> response = new HashMap<>();
        response.put("ids", ids);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/worksheet/{id}")
    public ResponseEntity<Map<String, Object>> getConceptDetail(@PathVariable Integer id) {
        WorksheetDetailResponseDto optionalConcept = worksheetService.getConceptById(id);
        Map<String, Object> response = new HashMap<>();

        if (optionalConcept != null) {
            response.put("code", 200);
            response.put("data", optionalConcept);
            response.put("msg", "개념 단건 조회 성공");
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 404);
            response.put("msg", "해당 개념이 존재하지 않음");
            return ResponseEntity.status(404).body(response);
        }
    }

    /**
     * 🎯 학습 완료 및 포인트 지급 API
     * - 하루 1회 포인트 지급 제한
     * - 중복 완료 방지
     */
    @PostMapping("/worksheet/complete")
    public ResponseEntity<Map<String, Object>> completeWorksheet(@RequestBody WorksheetCompleteRequestDto request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> result = worksheetService.completeWorksheet(
                request.getUserId(), 
                request.getWorksheetId(), 
                request.getPointsEarned()
            );
            
            response.put("code", 200);
            response.put("data", result);
            response.put("msg", "학습 완료 처리 성공");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "학습 완료 처리 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 📊 사용자 학습 진도 조회 API
     */
    @GetMapping("/worksheet/user/{userId}/progress")
    public ResponseEntity<Map<String, Object>> getUserProgress(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<Integer, Boolean> progress = worksheetService.getUserProgress(userId);
            
            response.put("code", 200);
            response.put("data", progress);
            response.put("msg", "학습 진도 조회 성공");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("code", 500);
            response.put("msg", "학습 진도 조회 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
