package com.moneykidsback.repository;

import com.moneykidsback.model.entity.Worksheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorksheetRepository extends JpaRepository<Worksheet, Integer> {
    List<Worksheet> findByDifficulty(int difficulty);
}