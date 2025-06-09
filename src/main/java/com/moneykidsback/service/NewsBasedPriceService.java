package com.moneykidsback.service;

import com.moneykidsback.model.entity.Article;
import com.moneykidsback.model.entity.Stock;
import com.moneykidsback.model.entity.StockPriceLog;
import com.moneykidsback.repository.ArticleRepository;
import com.moneykidsback.repository.StockRepository;
import com.moneykidsback.repository.StockPriceLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class NewsBasedPriceService {
    
    @Autowired
    private ArticleRepository articleRepository;
    
    @Autowired
    private StockRepository stockRepository;
    
    @Autowired
    private StockPriceLogRepository stockPriceLogRepository;
    
    private final Random random = new Random();
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    /**
     * 기사 기반 주가 변동 시작
     * 최근 생성된 기사들을 기반으로 점진적 주가 변동을 실행
     */
    @Async
    public void startNewsBasedPriceMovement() {
        try {
            // 오늘 생성된 모든 기사 조회
            List<Article> todayArticles = articleRepository.findByDate(LocalDate.now());
            
            if (todayArticles.isEmpty()) {
                System.out.println("📈 오늘 생성된 기사가 없어 주가 변동을 건너뜁니다.");
                return;
            }
            
            System.out.println("📊 기사 기반 주가 변동 시작 - 총 " + todayArticles.size() + "개 기사 반영");
            
            // 각 기사별로 출력하여 디버깅
            for (int i = 0; i < todayArticles.size(); i++) {
                Article article = todayArticles.get(i);
                System.out.println(String.format("   %d. [%s] %s (효과: %s)", 
                    i + 1, article.getStockId(), article.getTitle(), article.getEffect()));
            }
            
            // ✨ 모든 기사에 대해 병렬로 주가 변동 처리
            CompletableFuture<?>[] futures = todayArticles.stream()
                .map(article -> CompletableFuture.runAsync(() -> 
                    processArticleBasedPriceChange(article), executorService))
                .toArray(CompletableFuture[]::new);
            
            // 모든 변동 완료까지 대기
            CompletableFuture.allOf(futures).join();
            System.out.println("🎉 모든 종목의 기사 기반 주가 변동이 완료되었습니다!");
            
        } catch (Exception e) {
            System.err.println("❌ 기사 기반 주가 변동 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 개별 기사에 따른 주가 변동 처리
     * 점진적으로 4-8번에 걸쳐 목표가에 도달
     */
    private void processArticleBasedPriceChange(Article article) {
        try {
            Stock stock = stockRepository.findById(article.getStockId()).orElse(null);
            if (stock == null) {
                System.out.println("⚠️ 주식을 찾을 수 없음: " + article.getStockId());
                return;
            }
            
            // "기본 응답" 기사는 건너뛰기
            if (article.getTitle() != null && article.getTitle().contains("기본 응답")) {
                System.out.println("⚠️ [" + stock.getName() + "] 기본 응답 기사로 인해 주가 변동 생략");
                return;
            }
            
            System.out.println("🎯 [" + stock.getName() + "] 기사 영향 분석 시작");
            System.out.println("   기사 제목: " + article.getTitle());
            System.out.println("   기사 효과: " + article.getEffect());
            
            // 기사 효과에 따른 목표 변동률 결정
            double targetChangeRate = determineTargetChangeRate(article.getEffect());
            int currentPrice = stock.getPrice();
            int targetPrice = (int) (currentPrice * (1 + targetChangeRate));
            
            System.out.println("   현재가: " + currentPrice + "원");
            System.out.println("   목표가: " + targetPrice + "원 (변동률: " + String.format("%.2f", targetChangeRate * 100) + "%)");
            
            // 점진적 변동 실행 (4-8단계)
            executeGradualPriceChange(stock, currentPrice, targetPrice, article.getEffect());
            
        } catch (Exception e) {
            System.err.println("❌ 개별 기사 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 기사 효과에 따른 목표 변동률 결정
     */
    private double determineTargetChangeRate(String effect) {
        if (effect == null || effect.trim().isEmpty()) {
            System.out.println("   ⚠️ 기사 효과가 null 또는 빈 값이므로 중립으로 처리");
            return (random.nextDouble() - 0.5) * 0.04; // -2% ~ +2%
        }
        
        switch (effect.toLowerCase().trim()) {
            case "호재":
                return 0.03 + (random.nextDouble() * 0.07); // +3% ~ +10%
            case "악재":
                return -0.08 - (random.nextDouble() * 0.05); // -8% ~ -13%
            case "중립":
            default:
                return (random.nextDouble() - 0.5) * 0.04; // -2% ~ +2%
        }
    }
    
    /**
     * 점진적 주가 변동 실행
     * 여러 단계에 걸쳐 목표가에 도달하며, 중간에 역방향 변동도 포함
     */
    private void executeGradualPriceChange(Stock stock, int startPrice, int targetPrice, String newsEffect) {
        try {
            int steps = 4 + random.nextInt(5); // 4-8단계
            int currentPrice = startPrice;
            
            System.out.println("📈 [" + stock.getName() + "] " + steps + "단계에 걸친 점진적 변동 시작");
            
            for (int step = 1; step <= steps; step++) {
                // 단계별 목표가 계산 (약간의 변동성 포함)
                double progress = (double) step / steps;
                int stepTargetPrice = calculateStepPrice(startPrice, targetPrice, progress, step, steps);
                
                // 가격 변동 적용
                synchronized (stock) { // 동시성 문제 방지
                    stock.setBeforePrice(currentPrice);
                    stock.setPrice(stepTargetPrice);
                    stock.setUpdateAt(LocalDateTime.now());
                    stockRepository.save(stock);
                }
                
                // 변동률 계산 및 로그 저장
                int volatility = (int) ((stepTargetPrice - currentPrice) * 100.0 / currentPrice);
                saveStockPriceLog(stock, volatility);
                
                System.out.println("   [" + stock.getName() + "] " + step + "단계: " + currentPrice + "원 → " + stepTargetPrice + "원 " +
                    "(변동률: " + volatility + "%)");
                
                currentPrice = stepTargetPrice;
                
                // 단계 간 대기 시간 (15초 ~ 45초로 단축)
                if (step < steps) {
                    int waitTime = 15 + random.nextInt(30); // 15-45초
                    System.out.println("     [" + stock.getName() + "] ⏰ " + waitTime + "초 대기...");
                    Thread.sleep(waitTime * 1000);
                }
            }
            
            System.out.println("✅ [" + stock.getName() + "] 기사 기반 주가 변동 완료: " + 
                startPrice + "원 → " + currentPrice + "원");
                
        } catch (Exception e) {
            System.err.println("❌ [" + stock.getName() + "] 점진적 주가 변동 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 단계별 가격 계산 (변동성 포함)
     */
    private int calculateStepPrice(int startPrice, int targetPrice, double progress, int step, int totalSteps) {
        // 기본 진행률에 따른 가격
        int basePrice = (int) (startPrice + (targetPrice - startPrice) * progress);
        
        // 중간 단계에서 역방향 변동 가능성 (30% 확률)
        if (step > 1 && step < totalSteps && random.nextDouble() < 0.3) {
            // 역방향 변동 (-2% ~ +2%)
            double reverseChange = (random.nextDouble() - 0.5) * 0.04;
            basePrice = (int) (basePrice * (1 + reverseChange));
            System.out.println("     🌊 중간 변동성 적용: " + String.format("%.2f", reverseChange * 100) + "%");
        }
        
        // 최종 단계에서는 목표가에 더 가까이
        if (step == totalSteps) {
            basePrice = targetPrice + random.nextInt(21) - 10; // ±10원 오차
        }
        
        return Math.max(basePrice, 1); // 최소 1원
    }
    
    /**
     * 주가 변동 로그 저장
     */
    private void saveStockPriceLog(Stock stock, int volatility) {
        try {
            StockPriceLog log = new StockPriceLog();
            log.setStock(stock);
            log.setVolatility(volatility);
            log.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            stockPriceLogRepository.save(log);
        } catch (Exception e) {
            System.err.println("❌ 주가 로그 저장 실패: " + e.getMessage());
        }
    }
} 