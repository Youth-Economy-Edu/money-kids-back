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

    // 투자 성격 점수들
    @Column(name = "aggressiveness", nullable = false)
    private double aggressiveness;

    @Column(name = "assertiveness", nullable = false)
    private double assertiveness;

    @Column(name = "risk_neutrality", nullable = false)
    private double riskNeutrality;

    @Column(name = "security_oriented", nullable = false)
    private double securityOriented;

    @Column(name = "calmness", nullable = false)
    private double calmness;

    // 분석 결과의 유형 (예: 공격투자형, 적극투자형 등)
    @Column(name = "type", nullable = false)
    private String type;

    // 분석 결과에 대한 피드백
    @Column(name = "feedback")
    private String feedback;

    // 분석 결과 생성일시
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}