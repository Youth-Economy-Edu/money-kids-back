package com.moneykidsback.model.entity;

import jakarta.persistence.*;
import lombok.*;
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
    private String activityType;

    @Column(name = "user_quiz_id")
    private Integer userQuizId;

    @Column(name = "quiz_level")
    private String quizLevel;

    @Column(name = "quiz_category")
    private String quizCategory;

    @Column(name = "stocklog_id")
    private String stocklogId;

    @Column(name = "stock_company_size")
    private String stockCompanySize;

    @Column(name = "stock_category")
    private String stockCategory;

    @Column(name = "completion_id")
    private Integer completionId;

    @Column(name = "worksheet_difficulty")
    private String worksheetDifficulty;

    @Column(name = "worksheet_category")
    private String worksheetCategory;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
