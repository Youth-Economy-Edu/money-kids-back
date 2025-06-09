package com.moneykidsback.model.entity;

import com.moneykidsback.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "stock_log")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockLog {
    @Id
    @Column(name = "id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @Column(name = "date")
    private String date;

    @Column(name = "quantity")
    private int quantity;

    // 편의 생성자
    public StockLog(String id, String userId, String stockId, String date, int quantity) {
        this.id = id;
        this.date = date;
        this.quantity = quantity;
        // user와 stock은 별도로 설정해야 함
    }
}
