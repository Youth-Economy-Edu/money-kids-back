package com.moneykidsback.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderDetailResponse {
    private String stockName;
    private int quantity;
    private int total;
}