package com.moneykidsback.model.entity;
// 학생 활동 로그를 저장하는 테이블에 대응하는 JPA 엔티티
import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "activity_type", nullable = false)
    private String activityType; // QUIZ, STOCK_TRADE, CONTENT_COMPLETION

    // 퀴즈 관련 필드
    @Column(name = "user_quiz_id")
    private Integer userQuizId;

    @Column(name = "quiz_level")
    private String quizLevel;

    @Column(name = "quiz_category")
    private String quizCategory;

    // 주식 관련 필드
    @Column(name = "stocklog_id")
    private String stocklogId;

    @Column(name = "stock_company_size")
    private String stockCompanySize;

    @Column(name = "stock_category")
    private String stockCategory;

    // 학습지 관련 필드
    @Column(name = "completion_id")
    private Integer completionId;

    @Column(name = "worksheet_difficulty")
    private String worksheetDifficulty;

    @Column(name = "worksheet_category")
    private String worksheetCategory;

    // 공통 필드
    @Column(name = "status", nullable = false)
    private String status; // SUCCESS, FAIL, PARTIAL

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}