package com.moneykidsback.dto.request;


import com.moneykidsback.model.Stock;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

// 변동률 계산용 DTO
@Getter
@AllArgsConstructor
public class StockChangeRateDto {
    private String code;
    private String name;
    private int price;
    private String category;
    private LocalDateTime updateAt;
    private double changeRate; // 변동률(%)


    public static List<Stock> change;

}