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
    
    @EmbeddedId
    private UserStockId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("stockId")
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "total")
    private int total;

    // 편의 생성자
    public UserStock(String userId, String stockId, int quantity, int total) {
        this.id = new UserStockId(userId, stockId);
        this.quantity = quantity;
        this.total = total;
    }
}