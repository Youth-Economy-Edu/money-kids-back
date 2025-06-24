package com.moneykidsback.model.entity;

import com.moneykidsback.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "user_stock")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStock {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "stock_id")
    private String stockId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "stock_id", insertable = false, updatable = false)
    private Stock stock;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "total")
    private int total;

    // 편의 생성자
    public UserStock(String userId, String stockId, int quantity, int total) {
        this.userId = userId;
        this.stockId = stockId;
        this.quantity = quantity;
        this.total = total;
    }
}