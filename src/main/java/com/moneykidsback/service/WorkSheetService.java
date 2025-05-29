package com.moneykidsback.service;

import com.moneykidsback.model.DTO.Request.WorkSheetRequestDto;
import com.moneykidsback.model.entity.WorkSheet;
import com.moneykidsback.repository.WorkSheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkSheetService {

    @Autowired
    private WorkSheetRepository workSheetRepository;

    public List<WorkSheet> getConceptsByDifficulty(int level) {
        return workSheetRepository.findByDifficulty(level);
    }

    public Optional<WorkSheet> getConceptById(int conceptId) {
        return workSheetRepository.findById(conceptId);
    }

    public WorkSheet saveConcept(WorkSheetRequestDto dto) {
        WorkSheet newConcept = new WorkSheet();
        newConcept.setDifficulty(dto.getDifficulty());
        newConcept.setTitle(dto.getTitle());
        newConcept.setContent(dto.getContent());

        return workSheetRepository.save(newConcept);
    }
}
