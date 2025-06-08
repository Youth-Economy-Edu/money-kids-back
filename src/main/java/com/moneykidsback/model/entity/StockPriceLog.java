package com.moneykidsback.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "STOCKPRICELOG")
@Getter
@Setter
public class StockPriceLog {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String ID;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @Column(name = "price")
    private int price;

    @Column(name = "date")
    private String date; // 변동이 발생한 날짜

    @Column(name = "volatility")
    private double volatility; // 변동률
}
