package com.moneykidsback.model.entity;

import com.moneykidsback.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "USER_STOCK")
@IdClass(UserStockId.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    // 명시적 생성자 (AllArgsConstructor와 별도로 커스텀 가능)
    public UserStock(String userId, String stockId, int quantity, int total) {
        this.id = 0; // 또는 UUID/시퀀스/카운터 등
        this.userId = userId;
        this.stockId = stockId;
        this.quantity = quantity;
        this.total = total;
    }
}