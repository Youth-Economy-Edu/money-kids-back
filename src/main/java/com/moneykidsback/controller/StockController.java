package com.moneykidsback.controller;

import com.moneykidsback.model.dto.request.SaveWishlistDto;
import com.moneykidsback.model.dto.response.StockChangeRateDto;
import com.moneykidsback.model.entity.Stock;
import com.moneykidsback.service.RankingService;
import com.moneykidsback.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StockController {
    @Autowired
    private final RankingService rankingService;
    @Autowired
    private final WishlistService wishlistService;

    public StockController(RankingService rankingService, WishlistService wishlistService) {
        this.rankingService = rankingService;
        this.wishlistService = wishlistService;
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
    @GetMapping("/stocks/category")
    public ResponseEntity<List<Stock>> getStockRankingByCategory(@RequestParam(required = true) String category) {
        List<Stock> stocks;
        if (category.equals("IT")){
            stocks = rankingService.findAllByCategoryOrderByPriceDesc(category);
        } else if (category.equals("Medical")){
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
