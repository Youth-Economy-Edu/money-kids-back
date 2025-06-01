package com.moneykidsback.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

//todo: PostgreSQL 로 바꿔야됨
@Entity
@Getter
@Setter
@Table(name = "stock")
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int ID;
    @Column(name = "name")
    private String name;
    @Column(name = "price")
    private int price = 0;
    @Column(name = "category")
    private String category;

    //todo: 가격 변동률 떄문에 아래 두개컬럼 추가 논의 필요
    @Column(nullable = false)
    private int beforePrice =0;
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PreUpdate //엔티티가 수정되기 전 실행
    public void preUpdate() {
        this.updatedAt = java.time.LocalDateTime.now();
    }
}
