package com.moneykidsback.controller;

import java.util.List;

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

    @Autowired
    public StockController(RankingService rankingService, WishlistService wishlistService, StockService stockService) {
        this.rankingService = rankingService;
        this.wishlistService = wishlistService;
        this.stockService = stockService;
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

    // 위시리스트에서 주식 삭제
//    @DeleteMapping("/stocks/favorite")
//    public void deleteWishlist(@RequestBody SaveWishlistDto saveWishlistDto) {
//        wishlistService.deleteWishlist(saveWishlistDto);
//    }
}
