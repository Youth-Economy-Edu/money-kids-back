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
    @Column(name = "id")
    private String ID;
    @Column(name = "name")
    private String name;
    @Column(name = "price")
    private int price = 0;
    @Column(name = "category")
    private String category;

    //todo: 가격 변동률 떄문에 아래 두개컬럼 추가 논의 필요
    @Column(nullable = false)
    private int before_price =0;
    @Column(nullable = false)
    private LocalDateTime update_at;

    @PrePersist //엔티티가 수정되기 전 실행
    public void prePersist() {
        this.update_at = java.time.LocalDateTime.now();
    }

    @PreUpdate // 엔티티가 수정되기 전 실행
    public void preUpdate() {
        this.update_at = java.time.LocalDateTime.now();
    }
}
