package com.moneykidsback.controller;

import com.moneykidsback.model.entity.WorkSheet;
import com.moneykidsback.service.WorkSheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/worksheet")
public class WorkSheetController {

    @Autowired
    private WorkSheetService workSheetService;

    @GetMapping("/difficulty/{level}")
    public ResponseEntity<List<WorkSheet>> getConcepts(@PathVariable int level) {
        return ResponseEntity.ok(workSheetService.getConceptsByDifficulty(level));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkSheet> getConceptDetail(@PathVariable int id) {
        return workSheetService.getConceptById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
