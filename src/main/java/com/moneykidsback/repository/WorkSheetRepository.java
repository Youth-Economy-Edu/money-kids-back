package com.moneykidsback.repository;

import com.moneykidsback.model.entity.WorkSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkSheetRepository extends JpaRepository<WorkSheet, Integer> {
    List<WorkSheet> findByDifficulty(int difficulty);
}
