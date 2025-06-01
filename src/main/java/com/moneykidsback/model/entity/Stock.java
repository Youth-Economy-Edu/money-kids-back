package com.moneykidsback.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stock")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    // Q1: 주식이 새로 생성되거나 삭제되는 일이 있을 것인가?
    // A1: 없음. 종목 테이블은 fixed라고 생각할 것.
    @Id
    @Column(name = "ID")
    private String id; // 주식 코드

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private int price;

    @Column(name = "category")
    private String category;

    @Column(name = "size")
    private String size;
}
