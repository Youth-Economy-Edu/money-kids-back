package com.moneykidsback.repository;

import com.moneykidsback.model.entity.QuizRewardLog;
import com.moneykidsback.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface QuizRewardLogRepository extends JpaRepository<QuizRewardLog, Long> {
    /**
     * 특정 사용자가, 특정 날짜에, 특정 난이도의 보상을 받았는지 확인합니다.
     */
    boolean existsByUserAndRewardDateAndDifficulty(User user, LocalDate rewardDate, String difficulty);
}
