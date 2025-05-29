package com.moneykidsback.model.dto.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "USER_STOCK")
@IdClass(UserStockId.class)
@NoArgsConstructor
@AllArgsConstructor
public class UserStock {
    @Id
    private int id;

    @Id
    private String userId;

    @Id
    private String stockId;

    private int quantity;
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
