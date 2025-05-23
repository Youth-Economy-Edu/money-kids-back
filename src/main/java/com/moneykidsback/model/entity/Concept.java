package com.moneykidsback.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "Concept")
public class Concept {

    @Id
    @Column(name = "concept_id", nullable = false)
    private long concept_id; // 개념 고유 식별 아이디

    @Column(name = "difficulty_level")
    private int difficultyLevel; // 경제 개념 난이도 (1~5)

    @Column(name = "concept_title", nullable = false)
    private String concept_title; // 개념 제목 ex) 수요

    @Column(name = "concept_content", columnDefinition = "TEXT", nullable = false)
    private String concept_content; // 개념 설명 ex) 수요란 물건을 구입하고자...

    @Column(name = "concept_image_url", nullable = false)
    private String concept_image_url; // 이미지 URL (임시로 미정인 상태)
}
