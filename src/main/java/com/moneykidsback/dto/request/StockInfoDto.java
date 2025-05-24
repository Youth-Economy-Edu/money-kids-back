package com.moneykidsback.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 데이터 구조 맵핑 위한 DTO (DB 요청 용도 아님)
@Getter
@AllArgsConstructor
public class StockInfoDto {
    private String code;
    private String name;
    private int price;
    private String category;
}