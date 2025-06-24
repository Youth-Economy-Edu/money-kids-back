package com.moneykidsback.repository;

import com.moneykidsback.model.entity.User;
import com.moneykidsback.model.entity.UserQuizSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserQuizSessionRepository extends JpaRepository<UserQuizSession, Long> {
    
    // 특정 사용자의 특정 날짜, 특정 레벨의 퀴즈 세션 조회 (24시간 제한 체크용)
    Optional<UserQuizSession> findByUserAndQuizLevelAndCompletionDate(
            User user, Integer quizLevel, String completionDate);
    
    // 사용자의 모든 퀴즈 세션 조회
    List<UserQuizSession> findByUserOrderByCompletedAtDesc(User user);
    
    // 사용자의 특정 레벨 퀴즈 세션들 조회
    List<UserQuizSession> findByUserAndQuizLevelOrderByCompletedAtDesc(User user, Integer quizLevel);
    
    // 사용자의 오늘 완료한 퀴즈 세션 개수 조회
    @Query("SELECT COUNT(uqs) FROM UserQuizSession uqs WHERE uqs.user = :user AND uqs.completionDate = :date")
    Long countByUserAndCompletionDate(@Param("user") User user, @Param("date") String date);
}
