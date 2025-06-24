package com.moneykidsback.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moneykidsback.model.dto.request.SaveWishlistDto;
import com.moneykidsback.model.dto.response.StockChangeRateDto;
import com.moneykidsback.model.entity.Stock;
import com.moneykidsback.model.entity.StockPriceLog;
import com.moneykidsback.repository.StockPriceLogRepository;
import com.moneykidsback.service.RankingService;
import com.moneykidsback.service.StockService;
import com.moneykidsback.service.WishlistService;

/**
 * 📈 주식 정보 관리 컨트롤러
 * - 종목별 카테고리 분류
 * - 관심 주식(위시리스트) 관리
 * - 주식 순위 조회 (주가/변동률)
 * - 실시간 주가 정보 제공
 */
@RestController
@RequestMapping("/api")
public class StockController {
    
    private final RankingService rankingService;
    private final WishlistService wishlistService;
    private final StockService stockService;
    private final StockPriceLogRepository stockPriceLogRepository;

    @Autowired
    public StockController(RankingService rankingService, WishlistService wishlistService, StockService stockService, StockPriceLogRepository stockPriceLogRepository) {
        this.rankingService = rankingService;
        this.wishlistService = wishlistService;
        this.stockService = stockService;
        this.stockPriceLogRepository = stockPriceLogRepository;
    }

    // 전체 주식 목록 조회
    @GetMapping("/stocks")
    public ResponseEntity<List<Stock>> getAllStocks() {
        List<Stock> stocks = stockService.getAllStocks();
        if (stocks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(stocks);
    }

    // 주식 코드로 종목 찾기
    @GetMapping("/stocks/code/{id}")
    public List<Stock> getStocksById(@PathVariable String id) {
        return stockService.findByCode(id);
    }

    // 종목 이름으로 종목 찾기
    @GetMapping("/stocks/name")
    public List<Stock> getStocksByName(@RequestParam String name) {
        return stockService.findByName(name);
    }

    // 카테고리로 종목 찾기
    @GetMapping("/stocks/category")
    public List<Stock> getStocksByCategory(@RequestParam String category) {
        return stockService.findByCategory(category);
    }
    
    // 주식 정보 변경
    @PutMapping("/stocks/updatePrice")
    public Stock updateStockPrice(@RequestBody Stock stock) {
        return stockService.updateStock(stock);
    }

    //순위 조회
    @GetMapping("/stocks/ranking")
    public ResponseEntity<?> getStockRanking(@RequestParam String standard) {
        // 주가 기준 순위 조회
        if ("price".equalsIgnoreCase(standard)) {
            List<Stock> stocksPrice = rankingService.getStocksOrderedByPriceDesc();
            if (stocksPrice.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(stocksPrice);
        }
        // 변동률 기준 순위 조회
        else if ("changeRate".equalsIgnoreCase(standard)) {
            List<StockChangeRateDto> stocksRate = rankingService.getStocksOrderedByChangeRateDesc();
            if (stocksRate.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(stocksRate);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    //종목별 순위 조회
    @GetMapping("/stocks/category/ranking")
    public ResponseEntity<List<Stock>> getStockRankingByCategory(@RequestParam(required = true) String category) {
        List<Stock> stocks;
        if (category.equals("IT") || category.equals("Medical")){
            stocks = rankingService.findAllByCategoryOrderByPriceDesc(category);
        } else {
            stocks = rankingService.getStocksOrderedByPriceDesc(); //할당된 값 없으면 주가기준 순위 조회
        }
        if (stocks.isEmpty()) {
            return ResponseEntity.noContent().build(); //내용이 없음(204)
        }
        return ResponseEntity.ok(stocks); //정상 반환(200)
    }

    // 주식 위시리스트에 저장/삭제(토글)
    @PostMapping("/stocks/favorite")
    public void saveWishlist(@RequestBody SaveWishlistDto saveWishlistDto) {
        wishlistService.saveWishlist(saveWishlistDto);
    }

    // 위시리스트 조회
    @GetMapping("/stocks/favorite")
    public ResponseEntity<List<Stock>> getWishlistByUserId(@RequestParam String userId) {
        List<Stock> wishlist = wishlistService.getWishlistByUserId(userId);
        if (wishlist.isEmpty()) {
            return ResponseEntity.noContent().build(); // 내용이 없음(204)
        }
        return ResponseEntity.ok(wishlist); // 정상 반환(200)
    }

    // 🌟 특정 주식의 가격 변동 로그 조회 (최근 100개)
    @GetMapping("/stocks/{stockId}/price-log")
    public ResponseEntity<?> getStockPriceLogs(@PathVariable String stockId) {
        try {
            List<StockPriceLog> logs = stockPriceLogRepository.findTop100ByStock_IdOrderByIdDesc(stockId);
            if (logs.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            // 결과를 간단한 DTO 형태로 변환
            List<Map<String, Object>> data = logs.stream()
                .map(log -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("price", log.getPrice());
                    item.put("logTime", log.getDate());
                    return item;
                })
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("data", data);
            response.put("msg", "주가 로그 조회 성공");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("data", null);
            errorResponse.put("msg", "주가 로그 조회 실패: " + e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // 🌟 주식 히스토리 조회 (프론트엔드 차트용)
    @GetMapping("/stocks/{stockId}/history")
    public ResponseEntity<?> getStockHistory(@PathVariable String stockId, @RequestParam String range) {
        try {
            List<StockPriceLog> logs = stockPriceLogRepository.findByStock_IdOrderByDateDesc(stockId);
            if (logs.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            
            // range에 따른 데이터 필터링
            LocalDateTime startTime = LocalDateTime.now();
            switch (range) {
                case "1M":
                    startTime = startTime.minusMinutes(5);
                    break;
                case "5M":
                    startTime = startTime.minusMinutes(30);
                    break;
                case "10M":
                    startTime = startTime.minusHours(1);
                    break;
                case "1H":
                    startTime = startTime.minusHours(6);
                    break;
                case "1D":
                    startTime = startTime.minusDays(7);
                    break;
                case "1W":
                    startTime = startTime.minusWeeks(8);
                    break;
                case "1Mon":
                    startTime = startTime.minusMonths(6);
                    break;
                default:
                    startTime = startTime.minusHours(1);
            }
            
            final LocalDateTime filterTime = startTime;
            List<Map<String, Object>> historyData = logs.stream()
                .filter(log -> log.getDate().isAfter(filterTime))
                .map(log -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("price", log.getPrice());
                    item.put("timestamp", log.getDate());
                    return item;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(historyData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
