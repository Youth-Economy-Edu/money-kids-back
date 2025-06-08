package com.moneykidsback.model.dto.request;

import com.moneykidsback.model.entity.Stock;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RateForAiNewsDto {
    private String code;
    private String name;
    private String category;
    private double changeRate;

    public RateForAiNewsDto(String code, String name, String category , double changeRate) {
        this.code = code;
        this.name = name;
        this.category = category;
        this.changeRate = changeRate;
    }
}
