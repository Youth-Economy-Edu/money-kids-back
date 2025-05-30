package com.moneykidsback.service;

import com.moneykidsback.model.DTO.Response.WorkSheetDetailDto;
import com.moneykidsback.model.DTO.Response.WorkSheetResponseDto;
import com.moneykidsback.model.entity.Worksheet;
import com.moneykidsback.repository.WorkSheetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkSheetService {

    private final WorkSheetRepository workSheetRepository;

    // 목록 조회
    public List<WorkSheetResponseDto> getConceptsByDifficulty(int level) {
        return workSheetRepository.findByDifficulty(level).stream()
                .map(ws -> new WorkSheetResponseDto(ws.getId(), ws.getTitle()))
                .collect(Collectors.toList());
    }

    // 단건 조회

    public WorkSheetDetailDto getConceptById(Integer conceptId) {
        Optional<Worksheet> optionalWorkSheet = workSheetRepository.findById(conceptId);
        if (optionalWorkSheet.isPresent()) {
            Worksheet ws = optionalWorkSheet.get();
            WorkSheetDetailDto dto = new WorkSheetDetailDto();
            dto.setId(ws.getId());
            dto.setDifficulty(ws.getDifficulty());
            dto.setTitle(ws.getTitle());
            dto.setContent(ws.getContent());
            return dto;
        }
        return null;
    }

}
