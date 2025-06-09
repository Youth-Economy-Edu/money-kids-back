package com.moneykidsback.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

//todo: PostgreSQL 로 바꿔야됨
@Entity
@Table(name = "stock")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {
    // Q1: 주식이 새로 생성되거나 삭제되는 일이 있을 것인가?
    // A1: 없음. 종목 테이블은 fixed라고 생각할 것.
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "code")
    private String code; // 주식 코드

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private int price;

    @Column(name = "category")
    private String category;

    @Column(name = "size")
    private String size;

    //todo: 가격 변동률 때문에 아래 컬럼 추가 - 이전 가격 저장용
    @Column(name = "before_price", nullable = false)
    private int beforePrice = 0;

    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

    @PrePersist //엔티티가 생성되기 전 실행
    public void prePersist() {
        this.updateAt = LocalDateTime.now();
    }

    @PreUpdate // 엔티티가 수정되기 전 실행
    public void preUpdate() {
        this.updateAt = LocalDateTime.now();
    }
}
