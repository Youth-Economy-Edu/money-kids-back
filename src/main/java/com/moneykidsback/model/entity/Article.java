package com.moneykidsback.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "Article")
public class Article {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // 기사 ID

    @ManyToOne
    @JoinColumn(name = "stock_id", referencedColumnName = "ID", nullable = false)
    private Stock stockId; // 주식 ID (외래키로 사용될 수 있음, 주식 엔티티와 연관 관계 설정 가능)

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
