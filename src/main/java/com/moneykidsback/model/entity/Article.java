package com.moneykidsback.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "article")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id; // 기사 ID

    @Column(name = "stock_id")
    private String stockId; // 대상 주식 ID

    @Column(name = "title")
    private String title; // 기사 제목

    @Column(name = "date")
    private String date; // 기사 날짜

    @Column(name = "content")
    private String content; // 기사 내용

    @Column(name = "effect")
    private String effect; // 주식에 미치는 영향
}
