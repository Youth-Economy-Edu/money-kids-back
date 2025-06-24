package com.moneykidsback.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "article")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id; // MySQL 테이블에 맞게 Integer로 변경

    @Column(name = "stock_id")
    private String stockId; // 대상 주식 ID

    @Column(name = "date", nullable = false)
    private String date; // MySQL 테이블에 맞게 String으로 변경 (VARCHAR(20))

    @Column(name = "title", nullable = false)
    private String title; // 뉴스 제목

    @Column(name = "content")
    private String content; // 뉴스 내용

    @Column(name = "effect")
    private String effect; // MySQL 테이블에 맞게 String으로 변경 (VARCHAR(100))

    @Column(name = "sentiment")
    private String sentiment; // 감정 분석 (POSITIVE, NEGATIVE, NEUTRAL)

    @Column(name = "impact")
    private String impact; // 영향력 (STRONG, MEDIUM, WEAK)

    @PrePersist
    public void prePersist() {
        if (this.date == null) {
            this.date = LocalDateTime.now().toString(); // 날짜가 없으면 현재 날짜/시간으로 설정
        }
    }
}
