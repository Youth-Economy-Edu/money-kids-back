package com.moneykidsback.service;

import com.moneykidsback.model.entity.Stock;
import com.moneykidsback.model.entity.StockPriceLog;
import com.moneykidsback.repository.StockRepository;
import com.moneykidsback.repository.StockPriceLogRepository;
import com.moneykidsback.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StockService {

    @Autowired
    StockRepository stockRepository;

    @Autowired
    StockPriceLogRepository stockPriceLogRepository;

    @Autowired
    OpenAiService openAiService;

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public List<Stock> findByCode(String id) {
        return stockRepository.findAllById(List.of(id));
    }

    public List<Stock> findByName(String name) {
        return stockRepository.findByName(name);
    }

    public List<Stock> findByCategory(String category) {
        return stockRepository.findByCategory(category);
    }

    public Stock updateStock(Stock stock) {
        return stockRepository.save(stock);
    }

    // ❌ 기존 방식: OpenAI 직접 주가 변동 (비활성화)
    // 📰 새로운 방식: 기사 기반 주가 변동으로 전환됨
    // - NewsGenerateService: 30분마다 기사 생성
    // - NewsBasedPriceService: 10초마다 기사 기반 점진적 주가 변동
    
    // @Scheduled(fixedDelay = 10000) // 기존 10초 스케줄 비활성화
    public void updateStockPrices_DISABLED() {
        System.out.println("🔄 [INFO] 기존 OpenAI 직접 주가 변동 방식이 비활성화되었습니다.");
        System.out.println("📰 새로운 기사 기반 주가 변동 시스템이 활성화되어 있습니다:");
        System.out.println("   - NewsGenerateService: 30분마다 기사 자동 생성");
        System.out.println("   - NewsBasedPriceService: 10초마다 기사 기반 점진적 주가 변동");
        System.out.println("   - 수동 기사 생성: POST /api/articles/generate");
        
        /*
        // === 기존 코드 (주석 처리) ===
        System.out.println(">>> [SCHEDULED] updateStockPrices() 실행 시각: "
        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        List<Stock> stocks = stockRepository.findAll();  // 전체 주식 목록 조회

        if (stocks.isEmpty()) {
            System.out.println("  → DB에 저장된 Stock이 하나도 없습니다.");
            return;
        }
        System.out.println("  → Stock 리스트에 담긴 ID 목록: " +
            stocks.stream().map(Stock::getId).toList());

        for (Stock stock : stocks) {
            int oldPrice = stock.getPrice();
            int newPrice = openAiService.getRandomPriceFromOpenAi(oldPrice);

            // (1) newPrice와 oldPrice를 무조건 출력
            System.out.printf("    [%s] 기존가: %d원, OpenAI 반환가: %d원%n",
                    stock.getId(), oldPrice, newPrice);

            // (2) newPrice가 유효하지 않으면 건너뛰기
            if (newPrice <= 0) {
                System.out.printf("        → 신규가가 0 이하여서 저장/로그 생략%n");
                continue;
            }

            int volatility = (int) ((newPrice - oldPrice) * 100.0 / oldPrice);

            // (변경 전) 가격과 (변경 후) 가격, 변동률을 콘솔에 출력
            System.out.printf("    [%s] 이전가: %d원 → 신규가: %d원  (변동률: %d%%)%n",
                    stock.getId(), oldPrice, newPrice, volatility);

            // 가격 업데이트
            stock.setPrice(newPrice);
            stockRepository.save(stock);

            // 가격 변동 로그 저장
            StockPriceLog log = new StockPriceLog();
            log.setStock(stock);
            log.setVolatility(volatility);
            log.setPrice(newPrice);
            log.setDate(LocalDateTime.now());
            stockPriceLogRepository.save(log);

            // **여기서 반드시 보여주고 싶은 정보를 출력해 줍니다.**
            System.out.println(String.format(
                "[PRICE-CHANGE] Stock ID=%s | oldPrice=%d → newPrice=%d | volatility=%d%%",
                stock.getId(), oldPrice, newPrice, volatility
            ));
        }
        */
    }

    private String generateLogId() {
        return "LOG_" + System.currentTimeMillis();
    }
}
