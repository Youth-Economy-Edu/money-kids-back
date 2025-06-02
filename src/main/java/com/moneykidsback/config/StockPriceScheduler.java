package com.moneykidsback.config;

import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import lombok.RequiredArgsConstructor;
import com.moneykidsback.service.StockService;

@Component
@RequiredArgsConstructor
public class StockPriceScheduler {
    private final StockService stockService;

    // 10분마다 자동으로 주가 업데이트 실행(현재 30초)
    @Scheduled(fixedRate = 30000)
    public void autoUpdate() {
        stockService.updateStockPrices(); // StockService에서 주가 랜덤 변동 로직 호출
    }
}
