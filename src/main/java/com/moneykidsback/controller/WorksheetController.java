package com.moneykidsback.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/api/worksheet")
public class WorksheetController {

    @Autowired
    private WorksheetService worksheetService;

    // ✅ 난이도별 목록 (id + title만)
    @GetMapping("/difficulty/{level}")
    public ResponseEntity<Map<String, Object>> getConceptIds(@PathVariable int level) {
        List<WorksheetResponseDto> ids = worksheetService.getConceptsByDifficulty(level);

        Map<String, Object> response = new HashMap<>();
        response.put("ids", ids);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
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

}

