package com.moneykidsback.controller;

import com.moneykidsback.model.entity.Stock;
import com.moneykidsback.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController(value = "/stock")
public class StockController {
    @Autowired
    StockService stockService;

    // 주식 코드로 종목 찾기
    @GetMapping("/code")
    public List<Stock> getStocksByCode(
            @RequestParam String code
    ) {
        return stockService.findByCode(code);
    }

    // 종목 이름으로 종목 찾기
    @GetMapping("/name")
    public List<Stock> getStocksByName(
            @RequestParam String name
    ) {
        return stockService.findByName(name);
    }

    // 카테고리로 종목 찾기
    @GetMapping("/category")
    public List<Stock> getStocksByCategory(
            @RequestParam String category
    ) {
        return stockService.findByCategory(category);
    }

    // 주식 정보 변경
    @PutMapping("/updatePrice")
    public Stock updateStockPrice(
            @RequestBody Stock stock
    ) {
        return stockService.updateStock(stock);
    }
}
