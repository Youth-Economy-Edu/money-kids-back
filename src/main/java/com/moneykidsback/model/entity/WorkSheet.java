package com.moneykidsback.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "WorkSheet")
public class WorkSheet {
    @Id
    @Column(name = "concept_id", nullable = false)
    private int concept_id; // 개념 고유 식별 아이디

    @Column(name = "difficulty")
    private int difficulty; // 경제 개념 난이도 (1~5)

    @Column(name = "title", length = 255, nullable = false)
    private String title; // 개념 제목 ex) 수요

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content; // 개념 설명 ex) 수요란 물건을 구입하고자...

    // --- Getter ---
    public int getConcept_id() {
        return concept_id;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    // --- Setter ---
    public void setConcept_id(int concept_id) {
        this.concept_id = concept_id;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }
}


