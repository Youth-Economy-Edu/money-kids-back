package com.moneykidsback.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "user_quiz_session", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "quiz_level", "completion_date"}))
public class UserQuizSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "quiz_level", nullable = false)
    private Integer quizLevel;  // 퀴즈 난이도 (1-5)

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;  // 총 문제 수

    @Column(name = "correct_answers", nullable = false)
    private Integer correctAnswers;  // 정답 수

    @Column(name = "points_earned", nullable = false)
    private Integer pointsEarned;  // 획득한 포인트

    @Column(name = "completion_date", nullable = false)
    private String completionDate;  // 완료 날짜 (YYYY-MM-DD 형식)

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;  // 완료 시간

    @Column(name = "points_awarded", nullable = false)
    private Boolean pointsAwarded = false;  // 포인트 지급 여부
}
