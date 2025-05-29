package com.moneykidsback.model.entity;

import jakarta.persistence.*;
import lombok.*;

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
    @Column(name = "code")
    private String code; // 주식 코드

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private int price;

    @Column(name = "category")
    private String category;
}
