package com.moneykidsback.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TradeDetailResponse {
    private String stockId;
    private int quantity;
    private int total;
}