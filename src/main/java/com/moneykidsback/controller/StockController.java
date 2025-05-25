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
    public ResponseEntity<List<Stock>> getStockRanking(@RequestParam(required = true) String standard) {
        List<Stock> stocks;
        if(standard.equals("price")) {
            stocks = rankingService.getStocksOrderedByPriceDesc(); // 주가 기준 순위 조회
        } else {
            stocks = rankingService.getStocksOrderedByChangeRateDesc();  //todo: 변동률 기준 순위 메소드(논의 필요)
        }
        if (stocks.isEmpty()) {
            return ResponseEntity.noContent().build(); //내용이 없음(204)
        }
        return ResponseEntity.ok(stocks); //정상 반환(200)
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
