package com.moneykidsback.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TradeRequest {
    private String stockId;
    private int quantity;
}