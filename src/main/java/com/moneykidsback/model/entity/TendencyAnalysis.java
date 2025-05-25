package com.moneykidsback.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

// 클래스설명 한국어로
// 사용자의 행동 분석 결과를 저장하는 엔티티
// TODO: 데이터베이스 설계에 따라 필요한 필드 추가 논의!

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TendencyAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 ID
    private Long userId; // 사용자 ID
    private String type; // 분석 유형 (예: 안정형, 공격형)
    private double score; // 분석 점수
    private String feedback; // 분석 피드백
    private LocalDateTime createdAt; // 생성 시각

}
