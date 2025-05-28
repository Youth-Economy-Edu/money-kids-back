package com.moneykidsback.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// 클래스설명 한국어로
// 사용자의 행동 분석 결과를 저장하는 엔티티

@Entity
@Table(name = "TENDENCY_ANALYSIS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TendencyAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "aggressive_score", nullable = false)
    private double aggressiveScore;

    @Column(name = "active_score", nullable = false)
    private double activeScore;

    @Column(name = "neutral_score", nullable = false)
    private double neutralScore;

    @Column(name = "stable_seeking_score", nullable = false)
    private double stableSeekingScore;

    @Column(name = "stable_score", nullable = false)
    private double stableScore;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "score", nullable = false)
    private double score;

    @Column(name = "feedback")
    private String feedback;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}