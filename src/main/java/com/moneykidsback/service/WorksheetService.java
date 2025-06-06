package com.moneykidsback.service;


import com.moneykidsback.model.dto.response.WorksheetDetailResponseDto;
import com.moneykidsback.model.dto.response.WorksheetResponseDto;
import com.moneykidsback.model.entity.Worksheet;
import com.moneykidsback.repository.WorksheetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorksheetService {

    private final WorksheetRepository worksheetRepository;

    // 목록 조회
    public List<WorksheetResponseDto> getConceptsByDifficulty(int level) {
        return worksheetRepository.findByDifficulty(level).stream()
                .map(ws -> new WorksheetResponseDto(ws.getId(), ws.getTitle()))
                .collect(Collectors.toList());
    }

    // 단건 조회

    public WorksheetDetailResponseDto getConceptById(Integer conceptId) {
        Optional<Worksheet> optionalWorksheet = worksheetRepository.findById(conceptId);
        if (optionalWorksheet.isPresent()) {
            Worksheet ws = optionalWorksheet.get();
            WorksheetDetailResponseDto dto = new WorksheetDetailResponseDto();
            dto.setId(ws.getId());
            dto.setDifficulty(ws.getDifficulty());
            dto.setTitle(ws.getTitle());
            dto.setContent(ws.getContent());
            return dto;
        }
        return null;
    }

}

