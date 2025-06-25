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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 📈 주식 정보 관리 컨트롤러
 * - 종목별 카테고리 분류
 * - 관심 주식(위시리스트) 관리
 * - 주식 순위 조회 (주가/변동률)
 * - 실시간 주가 정보 제공
 */
@Tag(name = "Stock", description = "주식 정보 관리")
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

    @Operation(summary = "전체 주식 목록 조회", description = "모든 주식 목록을 조회합니다")
    @GetMapping("/stocks")
    public ResponseEntity<List<Stock>> getAllStocks() {
        List<Stock> stocks = stockService.getAllStocks();
        if (stocks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(stocks);
    }

    @Operation(summary = "주식 코드로 종목 찾기", description = "주식 코드로 특정 종목을 조회합니다")
    @GetMapping("/stocks/code/{id}")
    public List<Stock> getStocksById(
            @Parameter(description = "주식 코드", required = true) @PathVariable String id) {
        return stockService.findByCode(id);
    }

    @Operation(summary = "종목 이름으로 종목 찾기", description = "종목 이름으로 주식을 검색합니다")
    @GetMapping("/stocks/name")
    public List<Stock> getStocksByName(
            @Parameter(description = "종목 이름", required = true) @RequestParam String name) {
        return stockService.findByName(name);
    }

    @Operation(summary = "카테고리로 종목 찾기", description = "카테고리별로 주식 목록을 조회합니다")
    @GetMapping("/stocks/category")
    public List<Stock> getStocksByCategory(
            @Parameter(description = "카테고리", required = true) @RequestParam String category) {
        return stockService.findByCategory(category);
    }
    
    @Operation(summary = "주식 정보 변경", description = "주식 가격 정보를 업데이트합니다")
    @PutMapping("/stocks/updatePrice")
    public Stock updateStockPrice(@RequestBody Stock stock) {
        return stockService.updateStock(stock);
    }

    @Operation(summary = "주식 순위 조회", description = "주가 또는 변동률 기준으로 주식 순위를 조회합니다")
    @GetMapping("/stocks/ranking")
    public ResponseEntity<?> getStockRanking(
            @Parameter(description = "순위 기준 (price/changeRate)", required = true) @RequestParam String standard) {
        if ("price".equalsIgnoreCase(standard)) {
            List<Stock> stocksPrice = rankingService.getStocksOrderedByPriceDesc();
            if (stocksPrice.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(stocksPrice);
        }
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

    @Operation(summary = "카테고리별 주식 순위 조회", description = "특정 카테고리 내에서 주식 순위를 조회합니다")
    @GetMapping("/stocks/category/ranking")
    public ResponseEntity<List<Stock>> getStockRankingByCategory(
            @Parameter(description = "카테고리", required = true) @RequestParam String category) {
        List<Stock> stocks;
        if (category.equals("IT") || category.equals("Medical")){
            stocks = rankingService.findAllByCategoryOrderByPriceDesc(category);
        } else {
            stocks = rankingService.getStocksOrderedByPriceDesc();
        }
        if (stocks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(stocks);
    }

    @Operation(summary = "위시리스트 저장/삭제", description = "주식을 위시리스트에 추가하거나 제거합니다")
    @PostMapping("/stocks/favorite")
    public void saveWishlist(@RequestBody SaveWishlistDto saveWishlistDto) {
        wishlistService.saveWishlist(saveWishlistDto);
    }

    @Operation(summary = "위시리스트 조회", description = "사용자의 위시리스트를 조회합니다")
    @GetMapping("/stocks/favorite")
    public ResponseEntity<List<Stock>> getWishlistByUserId(
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId) {
        List<Stock> wishlist = wishlistService.getWishlistByUserId(userId);
        if (wishlist.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(wishlist);
    }

    @Operation(summary = "주식 가격 변동 로그 조회", description = "특정 주식의 최근 100개 가격 변동 로그를 조회합니다")
    @GetMapping("/stocks/{stockId}/price-log")
    public ResponseEntity<?> getStockPriceLogs(
            @Parameter(description = "주식 ID", required = true) @PathVariable String stockId) {
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

    @Operation(summary = "주식 히스토리 조회", description = "차트용 주식 히스토리 데이터를 조회합니다")
    @GetMapping("/stocks/{stockId}/history")
    public ResponseEntity<?> getStockHistory(
            @Parameter(description = "주식 ID", required = true) @PathVariable String stockId, 
            @Parameter(description = "조회 범위", required = true) @RequestParam String range) {
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
                default:
                    startTime = startTime.minusDays(1);
                    break;
            }
            
            final LocalDateTime filterTime = startTime;
            List<StockPriceLog> filteredLogs = logs.stream()
                .filter(log -> log.getDate().isAfter(filterTime))
                .collect(Collectors.toList());
            
            // 결과를 간단한 DTO 형태로 변환
            List<Map<String, Object>> data = filteredLogs.stream()
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
            response.put("msg", "주식 히스토리 조회 성공");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("data", null);
            errorResponse.put("msg", "주식 히스토리 조회 실패: " + e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
