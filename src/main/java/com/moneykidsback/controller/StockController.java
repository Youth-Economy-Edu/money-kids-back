package com.moneykidsback.controller;

import com.moneykidsback.model.Stock;
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

    //전체 순위 조회
    @GetMapping("/stocks/ranking")
    public List<Stock> getStockRanking() {
        return rankingService.getStocksOrderedByPriceDesc();
    }

    //카테고리별 순위 조회
    @GetMapping("/stocks/category")
    public ResponseEntity<List<Stock>> getStockRankingByCategory(@RequestParam(required = false) String standard) {
        List<Stock> stocks;
        if (standard == null || standard.isEmpty()) { //파라미터가 비어있으면 전체 순위 조회
            stocks = rankingService.getStocksOrderedByPriceDesc();
        } else {
            stocks = rankingService.findAllByCategoryOrderByPriceDesc(standard);
        }
        if (stocks.isEmpty()) {
            return ResponseEntity.noContent().build(); //내용이 없음(204)
        }
        return ResponseEntity.ok(stocks); //정상(200)
    }
}
