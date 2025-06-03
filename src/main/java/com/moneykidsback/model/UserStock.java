package com.moneykidsback.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "USER-STOCK")
@IdClass(UserStockId.class)
public class UserStock {
    @Id
    @Column(name = "ID")
    private Integer id;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "total")
    private int total;
}