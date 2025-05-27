package com.moneykidsback.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    // Q1: 주식이 새로 생성되거나 삭제되는 일이 있을 것인가?
    // A1: 없음. 종목 테이블은 fixed라고 생각할 것.
    @Id
    @Column(name = "code")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String code; // 주식 코드

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private int price;

    @Column(name = "category")
    private String category;
}
