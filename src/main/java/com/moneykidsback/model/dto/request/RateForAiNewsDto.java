package com.moneykidsback.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RateForAiNewsDto {
    private String code;
    private String name;
    private double changeRate;
}
