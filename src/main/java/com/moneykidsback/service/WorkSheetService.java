package com.moneykidsback.service;

import com.moneykidsback.model.DTO.Response.WorkSheetDetailDto;
import com.moneykidsback.model.DTO.Response.WorkSheetResponseDto;
import com.moneykidsback.model.entity.WorkSheet;
import com.moneykidsback.repository.WorkSheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorkSheetService {

    @Autowired
    private WorkSheetRepository workSheetRepository;

    // 목록 조회
    public List<WorkSheetResponseDto> getConceptsByDifficulty(int level) {
        return workSheetRepository.findByDifficulty(level).stream()
                .map(ws -> new WorkSheetResponseDto(ws.getId(), ws.getTitle()))
                .collect(Collectors.toList());
    }

    // 단건 조회

    public Optional<WorkSheetDetailDto> getConceptById(int conceptId) {
        Optional<WorkSheet> optionalWorkSheet = workSheetRepository.findById(conceptId);

        return optionalWorkSheet.map(ws -> {
            WorkSheetDetailDto dto = new WorkSheetDetailDto();
            dto.setId(ws.getId());
            dto.setDifficulty(ws.getDifficulty());
            dto.setTitle(ws.getTitle());
            dto.setContent(ws.getContent());
            return dto;
        });
    }

}
