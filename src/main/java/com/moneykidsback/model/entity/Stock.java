package com.moneykidsback.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

//todo: PostgreSQL 로 바꿔야됨
@Table(name = "stock")
@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    // Q1: 주식이 새로 생성되거나 삭제되는 일이 있을 것인가?
    // A1: 없음. 종목 테이블은 fixed라고 생각할 것.
    @Id
    @Column(name = "id")
    private String ID;
    @Column(name = "code")
    private String code; // 주식 코드

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private int price = 0;
    @Column(name = "category")
    private String category;

    @Column(name = "size")
    private String size;

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
