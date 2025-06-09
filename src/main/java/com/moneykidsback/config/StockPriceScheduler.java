package com.moneykidsback.config;

import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import lombok.RequiredArgsConstructor;
import com.moneykidsback.service.StockService;

@Component
@RequiredArgsConstructor
public class StockPriceScheduler {
    private final StockService stockService;

    // 기사 기반 주가 변동 시스템으로 대체하여 기존 30초 자동 업데이트 비활성화
    // @Scheduled(fixedDelay = 30000)
    // public void autoUpdate() {
    //     stockService.updateStockPrices(); // StockService에서 주가 랜덤 변동 로직 호출
    // }
    
    // 필요시 수동 테스트용으로 활성화 가능
    // 수동 주가 업데이트 (테스트용)
    public void manualUpdate() {
        System.out.println("🔧 [MANUAL] 수동 주가 업데이트 실행");
        stockService.updateStockPrices();
    }
}
