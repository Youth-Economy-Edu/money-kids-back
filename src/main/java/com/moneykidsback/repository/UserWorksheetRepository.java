package com.moneykidsback.repository;

import com.moneykidsback.model.entity.UserWorksheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserWorksheetRepository extends JpaRepository<UserWorksheet, Long> {
    
    /**
     * 사용자의 특정 학습지 오늘 완료 기록 조회
     */
    Optional<UserWorksheet> findByUserIdAndWorksheetIdAndCompletionDate(
        String userId, Integer worksheetId, LocalDate completionDate);
    
    /**
     * 사용자의 특정 학습지 가장 최근 완료 기록 조회 (처음 학습인지 재학습인지 확인용)
     */
    Optional<UserWorksheet> findFirstByUserIdAndWorksheetIdOrderByCompletionDateDesc(
        String userId, Integer worksheetId);
    
    /**
     * 사용자의 모든 학습 완료 기록 조회
     */
    List<UserWorksheet> findByUserId(String userId);
    
    /**
     * 사용자의 완료한 학습지 ID 목록 조회
     */
    @Query("SELECT DISTINCT uw.worksheetId FROM UserWorksheet uw WHERE uw.userId = :userId")
    List<Integer> findCompletedWorksheetIdsByUserId(@Param("userId") String userId);
}
