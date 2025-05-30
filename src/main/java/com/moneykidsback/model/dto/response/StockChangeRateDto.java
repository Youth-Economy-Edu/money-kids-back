package com.moneykidsback.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// 변동률 계산용 DTO
@Getter
@Setter
@AllArgsConstructor
public class StockChangeRateDto {
    private String code;
    private String name;
    private int price;
    private String category;
    private LocalDateTime updateAt;
    private double changeRate; // 변동률(%)

}