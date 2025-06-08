package com.moneykidsback.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TradeHistoryResponse {
    private String stockId;
    private String date;
    private int quantity;
}
