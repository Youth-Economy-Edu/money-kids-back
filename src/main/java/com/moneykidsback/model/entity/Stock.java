package com.moneykidsback.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Stock {
    // Q1: 이거 객체는 그냥 Model안에 바로 집어넣는 건가?
    // Q2: 주식이 새로 생성되거나 삭제되는 일이 있을 것인가? 현실이라면 있겠지만 이건 가상이라서...
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
