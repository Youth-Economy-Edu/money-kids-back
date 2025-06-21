package com.moneykidsback.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * 퀴즈 만점 보상 지급 기록을 위한 엔티티
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "quiz_reward_log", uniqueConstraints = {
    // 한 명의 유저가 특정 난이도를 하루에 한 번만 완료할 수 있도록 DB 레벨에서 제약
    @UniqueConstraint(columnNames = {"user_id", "reward_date", "difficulty"})
})
public class QuizRewardLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User 엔티티와 다대일(N:1) 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "reward_date", nullable = false)
    private LocalDate rewardDate;

    @Column(name = "difficulty", nullable = false)
    private String difficulty;

    @Column(name = "reward_points", nullable = false)
    private int rewardPoints;

    // 엔티티가 생성될 때의 시간이 자동으로 기록됨
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;
}
