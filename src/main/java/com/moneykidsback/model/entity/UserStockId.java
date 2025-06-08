package com.moneykidsback.model.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStockId implements Serializable {
    private int id;
    private String userId;
    private String stockId;
}
