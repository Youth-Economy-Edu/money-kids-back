package com.moneykidsback.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "STOCKLOG")
@IdClass(StockLogId.class)
@NoArgsConstructor
@AllArgsConstructor
public class StockLog {
    @Id
    private String id;

    @Id
    private String userId;

    @Id
    private String stockId;

    private String date;
    private int quantity;
}
