package com.moneykidsback.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "article")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id; // 기사 ID

    @Column(name = "stock_id")
    private String stockId; // 대상 주식 ID

    @Column(name = "date", nullable = false)
    private LocalDate date; // 뉴스 날짜

    @Column(name = "title", nullable = false)
    private String title; // 뉴스 제목

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content; // 뉴스 내용

    @Column(name = "effect", nullable = false)
    private String effect; // 뉴스 효과 (긍정적/부정적/중립적)

    @PrePersist
    public void prePersist() {
        if (this.date == null) {
            this.date = LocalDate.now(); // 날짜가 없으면 현재 날짜로 설정
        }
    }
}
