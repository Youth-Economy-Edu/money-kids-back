package com.moneykidsback.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "STOCKPRICELOG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockPriceLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String ID;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @Column(name = "volatility")
    private int volatility;
    @Column(name = "price")
    private int price;

    @Column(name = "date")
    private String date;
}
