package com.moneykidsback.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 데이터 구조 맵핑 위한 DTO (DB 요청 용도 아님)
@Getter
@AllArgsConstructor
public class StockInfoDto {
    private String code;
    private String name;
    private int price;
    private String category;
}