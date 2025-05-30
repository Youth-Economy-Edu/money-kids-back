package com.moneykidsback.controller;

import com.moneykidsback.model.DTO.Response.WorkSheetDetailDto;
import com.moneykidsback.model.DTO.Response.WorkSheetResponseDto;
//import com.moneykidsback.model.entity.Worksheet;
import com.moneykidsback.service.WorkSheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/worksheet")
public class WorkSheetController {

    @Autowired
    private WorkSheetService workSheetService;

    // ✅ 난이도별 목록 (id + title만)
    @GetMapping("/difficulty/{level}")
    public ResponseEntity<Map<String, Object>> getConceptIds(@PathVariable int level) {
        List<WorkSheetResponseDto> ids = workSheetService.getConceptsByDifficulty(level);

        Map<String, Object> response = new HashMap<>();
        response.put("ids", ids);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getConceptDetail(@PathVariable Integer id) {
        WorkSheetDetailDto optionalConcept = workSheetService.getConceptById(id);
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

