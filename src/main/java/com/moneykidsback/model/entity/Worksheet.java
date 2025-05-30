package com.moneykidsback.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class Worksheet {
    @Id
    private Integer id; // 개념 고유 식별 아이디

    @Column(name = "difficulty")
    private int difficulty; // 경제 개념 난이도 (1~5)

    @Column(name = "title", length = 255, nullable = false)
    private String title; // 개념 제목 ex) 수요

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content; // 개념 설명 ex) 수요란 물건을 구입하고자...
}


