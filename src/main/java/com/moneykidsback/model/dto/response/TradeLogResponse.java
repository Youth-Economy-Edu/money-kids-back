package com.moneykidsback.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TradeLogResponse {
    private String stockId;
    private int quantity;
    private String date;
}
