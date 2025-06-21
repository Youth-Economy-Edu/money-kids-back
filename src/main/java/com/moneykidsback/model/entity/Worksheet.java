package com.moneykidsback.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Worksheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID 자동 생성 전략 추가
    private Integer id;

    @Column(name = "difficulty")
    private int difficulty;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
}
