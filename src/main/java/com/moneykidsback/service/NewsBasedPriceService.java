package com.moneykidsback.service;

import com.moneykidsback.model.entity.Article;
import com.moneykidsback.model.entity.Stock;
import com.moneykidsback.model.entity.StockPriceLog;
import com.moneykidsback.repository.ArticleRepository;
import com.moneykidsback.repository.StockRepository;
import com.moneykidsback.repository.StockPriceLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 📰 기사 기반 주가 변동 서비스
 * - 기사 발행 시 목표 등락률 설정
 * - 10초마다 6.5:3.5 확률로 0.02%~0.2% 변동
 * - 목표치에 도달하거나 영향력 시간 만료 시 종료
 */
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

    // 현재 활성화된 기사별 주가 변동 정보
    private final Map<String, PriceMovementInfo> activePriceMovements = new ConcurrentHashMap<>();

    /**
     * 기사별 주가 변동 정보 클래스
     */
    private static class PriceMovementInfo {
        public String stockId;
        public double targetChangeRate; // 목표 등락률 (%)
        public double currentProgress; // 현재 진행률 (%)
        public LocalDateTime startTime; // 시작 시간
        public LocalDateTime endTime; // 종료 시간
        public int basePrice; // 시작 가격
        public String sentiment; // 감정 (긍정/부정/중립)
        public String impact; // 영향도 (강함/보통/약함)
        
        public PriceMovementInfo(String stockId, double targetChangeRate, int basePrice, 
                               String sentiment, String impact) {
            this.stockId = stockId;
            this.targetChangeRate = targetChangeRate;
            this.currentProgress = 0.0;
            this.startTime = LocalDateTime.now();
            this.basePrice = basePrice;
            this.sentiment = sentiment;
            this.impact = impact;
            
            // 영향 지속 시간 설정 (영향도에 따라)
            int durationHours = switch (impact) {
                case "강함" -> 6; // 6시간
                case "보통" -> 3; // 3시간
                case "약함" -> 1; // 1시간
                default -> 2; // 기본 2시간
            };
            
            this.endTime = this.startTime.plusHours(durationHours);
        }
        
        public boolean isExpired() {
            return LocalDateTime.now().isAfter(endTime);
        }
        
        public boolean isTargetReached() {
            return Math.abs(currentProgress) >= Math.abs(targetChangeRate * 0.9); // 90% 도달시 완료
        }
    }

    /**
     * 기사에 대한 주가 변동 시작
     */
    public void startPriceMovementForArticle(Article article) {
        try {
            double targetEffect = Double.parseDouble(article.getEffect()); // String을 Double로 파싱
            
            PriceMovementInfo movement = new PriceMovementInfo(
                article.getStockId(),
                targetEffect, // 파싱된 Double 값 사용
                getCurrentStockPrice(article.getStockId()),
                article.getSentiment(),
                article.getImpact()
            );
            
            activePriceMovements.put(String.valueOf(article.getId()), movement); // Integer를 String으로 변환
            
            System.out.println(String.format(
                "📈 주가 변동 시작: %s | 목표: %+.2f%% | 지속시간: %s시간 | 기사: %s",
                article.getStockId(),
                movement.targetChangeRate,
                movement.endTime.getHour() - movement.startTime.getHour(),
                article.getTitle()
            ));
        } catch (NumberFormatException e) {
            System.err.println("❌ 기사 효과 파싱 실패: " + article.getEffect() + " - " + e.getMessage());
        }
    }

    /**
     * 모든 활성 기사의 주가 변동 시작 (컨트롤러에서 호출)
     */
    public void startNewsBasedPriceMovement() {
        System.out.println("📰 기사 기반 주가 변동 시스템 활성화");
        // 실제로 이 메서드는 스케줄러가 자동으로 처리하므로 별도 로직 불필요
    }

    /**
     * 10초마다 기사 기반 주가 변동 실행
     */
    @Scheduled(fixedDelay = 10000) // 10초
    public void processNewsBasedPriceMovements() {
        System.out.println("📊 [SCHEDULED] 주가 변동 처리: " + LocalDateTime.now());
        System.out.println("    활성 기사 수: " + activePriceMovements.size());

        // 만료되거나 목표 달성한 변동 제거
        activePriceMovements.entrySet().removeIf(entry -> {
            PriceMovementInfo movement = entry.getValue();
            if (movement.isExpired() || movement.isTargetReached()) {
                System.out.println("    ✅ 변동 완료: " + entry.getKey() + " (진행률: " + 
                                 String.format("%.2f%%", movement.currentProgress) + ")");
                return true;
            }
            return false;
        });

        // 각 활성 변동에 대해 주가 업데이트
        for (Map.Entry<String, PriceMovementInfo> entry : activePriceMovements.entrySet()) {
            try {
                updateStockPriceBasedOnNews(entry.getKey(), entry.getValue());
            } catch (Exception e) {
                System.err.println("❌ 주가 변동 실패: " + entry.getKey() + " - " + e.getMessage());
            }
        }

        // 기사가 없는 주식들도 일반적인 변동성 적용
        applyGeneralMarketVolatility();
    }

    /**
     * 개별 주식의 주가를 기사 기반으로 변동
     */
    private void updateStockPriceBasedOnNews(String articleId, PriceMovementInfo movement) {
        Stock stock = stockRepository.findById(movement.stockId).orElse(null);
        if (stock == null) {
            System.err.println("주식을 찾을 수 없음: " + movement.stockId);
            return;
        }

        int oldPrice = stock.getPrice();
        
        // 변동률 계산 (6.5:3.5 확률)
        boolean moveTowardsTarget = random.nextDouble() < 0.65; // 65% 확률
        
        // 변동 크기 (0.02% ~ 0.2%)
        double changeRate = 0.02 + (random.nextDouble() * 0.18); // 0.02% ~ 0.2%
        
        // 영향도에 따른 변동 크기 조정
        double impactMultiplier = switch (movement.impact) {
            case "강함" -> 1.5;
            case "보통" -> 1.0;
            case "약함" -> 0.6;
            default -> 1.0;
        };
        changeRate *= impactMultiplier;
        
        // 목표 방향 결정
        double targetSign = movement.targetChangeRate > 0 ? 1.0 : -1.0;
        double actualChangeRate;
        
        if (moveTowardsTarget) {
            // 목표 방향으로 변동
            actualChangeRate = changeRate * targetSign;
        } else {
            // 반대 방향으로 변동 (시장 변동성 반영)
            actualChangeRate = changeRate * (-targetSign);
        }
        
        // 목표치 초과 방지
        double newProgress = movement.currentProgress + actualChangeRate;
        if (Math.abs(newProgress) > Math.abs(movement.targetChangeRate)) {
            actualChangeRate = movement.targetChangeRate - movement.currentProgress;
            newProgress = movement.targetChangeRate;
        }
        
        // 새 가격 계산
        int newPrice = (int) Math.round(movement.basePrice * (1 + newProgress / 100.0));
        newPrice = Math.max(1, newPrice); // 최소 1원 보장
        
        // 진행률 업데이트
        movement.currentProgress = newProgress;
        
        // 주가 업데이트
        stock.setPrice(newPrice);
        stockRepository.save(stock);
        
        // 로그 저장
        StockPriceLog log = new StockPriceLog();
        log.setStock(stock);
        log.setPrice(newPrice);
        log.setVolatility((int) Math.round(actualChangeRate * 100)); // 변동률을 정수로 저장
        log.setDate(LocalDateTime.now());
        stockPriceLogRepository.save(log);
        
        // 로그 출력
        System.out.println(String.format(
            "    📊 [%s] %s: %,d원 → %,d원 (%+.3f%%) [진행률: %.2f%% / %.2f%%] %s",
            movement.stockId,
            stock.getName(),
            oldPrice,
            newPrice,
            actualChangeRate,
            movement.currentProgress,
            movement.targetChangeRate,
            moveTowardsTarget ? "🎯목표방향" : "🔄반대방향"
        ));
    }

    /**
     * 현재 주식 가격 조회
     */
    private int getCurrentStockPrice(String stockId) {
        Stock stock = stockRepository.findById(stockId).orElse(null);
        return stock != null ? stock.getPrice() : 10000; // 기본값 10,000원
    }

    /**
     * 현재 활성 변동 상태 조회 (디버그용)
     */
    public Map<String, PriceMovementInfo> getActivePriceMovements() {
        return new ConcurrentHashMap<>(activePriceMovements);
    }

    /**
     * 특정 기사의 변동 중단
     */
    public void stopPriceMovementForArticle(String articleId) {
        PriceMovementInfo removed = activePriceMovements.remove(articleId);
        if (removed != null) {
            System.out.println("🛑 주가 변동 중단: " + articleId);
        }
    }

    /**
     * 모든 변동 중단 (긴급 상황용)
     */
    public void stopAllPriceMovements() {
        int count = activePriceMovements.size();
        activePriceMovements.clear();
        System.out.println("🚨 모든 주가 변동 중단됨: " + count + "개");
    }

    /**
     * 기사가 없는 주식들에 일반적인 시장 변동성 적용 (±2% 범위, 현실적 변동)
     */
    private void applyGeneralMarketVolatility() {
        List<Stock> allStocks = stockRepository.findAll();
        
        for (Stock stock : allStocks) {
            // 현재 기사 기반 변동이 활성화된 주식은 제외
            boolean hasActiveNews = activePriceMovements.values().stream()
                    .anyMatch(movement -> movement.stockId.equals(stock.getId()));
            
            if (!hasActiveNews) {
                // 15% 확률로 변동 적용 (변동 빈도 더 줄임)
                if (random.nextDouble() < 0.15) {
                    applyGeneralPriceChange(stock);
                }
            }
        }
    }

    /**
     * 개별 주식에 현실적인 변동성 적용 (일일 최대 ±5% 제한)
     */
    private void applyGeneralPriceChange(Stock stock) {
        int oldPrice = stock.getPrice();
        int basePrice = stock.getBeforePrice() > 0 ? stock.getBeforePrice() : oldPrice;
        
        // 현재 일일 변동률 계산
        double currentDailyChange = ((double)(oldPrice - basePrice) / basePrice) * 100;
        
        // 일일 최대 변동률 ±5% 제한
        if (Math.abs(currentDailyChange) >= 29.9) {
            System.out.printf("🚫 일일 변동률 제한: %s 현재 %.2f%% (변동 중단)%n", 
                             stock.getName(), currentDailyChange);
            return;
        }
        
        // 더 작은 변동률 분포 (최대 0.8%)
        double changeRate;
        double random1 = random.nextDouble();
        
        if (random1 < 0.7) {
            // 70% 확률로 아주 소폭 변동 (0.01% ~ 0.2%)
            changeRate = 0.01 + (random.nextDouble() * 0.19);
        } else if (random1 < 0.95) {
            // 25% 확률로 소폭 변동 (0.2% ~ 0.5%)
            changeRate = 0.2 + (random.nextDouble() * 0.3);
        } else {
            // 5% 확률로 중간 변동 (0.5% ~ 0.8%)
            changeRate = 0.5 + (random.nextDouble() * 0.3);
        }
        
        // 50:50 확률로 상승/하락
        boolean isIncrease = random.nextBoolean();
        if (!isIncrease) {
            changeRate = -changeRate;
        }
        
        // 새로운 변동률이 일일 제한을 초과하지 않도록 확인
        double newDailyChange = currentDailyChange + changeRate;
        if (Math.abs(newDailyChange) > 5.0) {
            // 제한에 맞춰 조정
            if (newDailyChange > 5.0) {
                changeRate = 5.0 - currentDailyChange;
            } else if (newDailyChange < -5.0) {
                changeRate = -5.0 - currentDailyChange;
            }
        }
        
        // 변동률이 너무 작으면 적용하지 않음
        if (Math.abs(changeRate) < 0.005) {
            return;
        }
        
        // 새 가격 계산
        int newPrice = (int) Math.round(oldPrice * (1 + changeRate / 100.0));
        
        // 최소 가격 보장 (100원 이상)
        if (newPrice < 100) {
            newPrice = 100;
        }
        
        // beforePrice 설정 (하루 시작가 유지)
        if (stock.getBeforePrice() <= 0) {
            stock.setBeforePrice(oldPrice);
        }
        
        // 가격 업데이트
        stock.setPrice(newPrice);
        stockRepository.save(stock);
        
        // 로그 저장
        StockPriceLog log = StockPriceLog.builder()
                .stock(stock)
                .price(newPrice)
                .volatility((int) Math.round(changeRate * 100))
                .date(LocalDateTime.now())
                .build();
        stockPriceLogRepository.save(log);
        
        // 현재 일일 변동률 계산
        double finalDailyChange = ((double)(newPrice - stock.getBeforePrice()) / stock.getBeforePrice()) * 100;
        
        System.out.printf("📈 일반 변동: %s %,d원 → %,d원 (단계: %+.2f%%, 일일: %+.2f%%)%n", 
                         stock.getName(), oldPrice, newPrice, changeRate, finalDailyChange);
    }
}
