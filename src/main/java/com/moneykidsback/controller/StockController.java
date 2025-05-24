package com.moneykidsback.controller;

import com.moneykidsback.model.Stock;
import com.moneykidsback.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/api")
public class StockController {
    @Autowired
    private RankingService rankingService;

    @GetMapping("/stocks/ranking")
    public List<Stock> getStockRanking() {
        return rankingService.getStocksOrderedByPriceDesc();
    }
}
