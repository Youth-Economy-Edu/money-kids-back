package com.moneykidsback.config;

import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import lombok.RequiredArgsConstructor;
import com.moneykidsback.service.StockService;
import com.moneykidsback.service.NewsGenerateService;

@Component
@RequiredArgsConstructor
public class StockPriceScheduler {
    private final StockService stockService;
    private final NewsGenerateService newsGenerateService;

    // ❌ 기존 주가 변동 시스템 비활성화 (2024년 새로운 기사 기반 시스템으로 교체)
    // 📰 새로운 시스템: NewsGenerateService + NewsBasedPriceService
    // - NewsGenerateService: 30분마다 기사 자동 생성 (@Scheduled)
    // - NewsBasedPriceService: 10초마다 기사 기반 점진적 주가 변동 (@Scheduled)
    
    // === 기존 방식 (비활성화) ===
    // @Scheduled(fixedDelay = 30000)
    // public void autoUpdate() {
    //     stockService.updateStockPrices(); // 기존 방식
    // }
    
    /**
     * 🔧 수동 테스트용 메서드 (개발/테스트 환경에서만 사용)
     * 기존 방식이 아닌 새로운 기사 기반 시스템 상태를 안내
     */
    public void manualSystemStatusCheck() {
        System.out.println("🔧 [MANUAL] 주가 시스템 상태 확인");
        System.out.println("📊 현재 활성화된 시스템:");
        System.out.println("   ✅ NewsGenerateService: 30분마다 기사 자동 생성");
        System.out.println("   ✅ NewsBasedPriceService: 10초마다 기사 기반 점진적 주가 변동");
        System.out.println("❌ 비활성화된 시스템:");
        System.out.println("   ❌ StockService.updateStockPrices() - 기존 OpenAI 직접 호출 방식");
        System.out.println("");
        System.out.println("💡 수동 기사 생성 테스트: POST /api/articles/generate");
    }

    /**
     * 🚀 수동 기사 생성 트리거 (테스트용)
     */
    public void manualNewsGeneration() {
        System.out.println("🚀 [MANUAL] 수동 기사 생성 실행");
        try {
            newsGenerateService.generateAndSaveNewsForAllStocks();
            System.out.println("✅ 수동 기사 생성 완료");
        } catch (Exception e) {
            System.err.println("❌ 수동 기사 생성 실패: " + e.getMessage());
        }
    }
}
