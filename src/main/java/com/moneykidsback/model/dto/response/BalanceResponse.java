package com.moneykidsback.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BalanceResponse {
    private int balance;        // 현금 잔고 (포인트)
    private int totalAsset;     // 총 자산 (현금 + 주식 평가금액)
    private int profit;         // 수익/손실
    private double rate;        // 수익률 (%)
    
    // 기존 생성자 호환성을 위한 생성자
    public BalanceResponse(int balance) {
        this.balance = balance;
        this.totalAsset = balance;
        this.profit = 0;
        this.rate = 0.0;
    }
}
