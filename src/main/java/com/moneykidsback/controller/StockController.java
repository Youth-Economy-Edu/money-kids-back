package com.moneykidsback.controller;

import com.moneykidsback.model.dto.response.StockChangeRateDto;
import com.moneykidsback.model.entity.Stock;
import com.moneykidsback.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StockController {
    @Autowired
    private RankingService rankingService;


    //todo: 주가, 등락율 순위 API 하나로 합치기

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
}
