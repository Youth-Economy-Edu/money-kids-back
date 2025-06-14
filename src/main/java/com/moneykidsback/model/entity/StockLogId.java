package com.moneykidsback.model.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockLogId implements Serializable {
    private String id;
    private String userId;
    private String stockId;
}
