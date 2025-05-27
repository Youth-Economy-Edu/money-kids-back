package com.moneykidsback.controller;

import com.moneykidsback.model.entity.Stock;
import com.moneykidsback.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController(value = "/stock")
public class StockController {
    @Autowired
    StockService stockService;

    // 주식 코드로 종목 찾기
    @GetMapping("/code")
    public List<Stock> getStocksByCode(
            @RequestBody String code
    ) {
        return stockService.findByCode(code);
    }

    // 종목 이름으로 종목 찾기
    @GetMapping("/name")
    public List<Stock> getStocksByName(
            @RequestBody String name
    ) {
        return stockService.findByName(name);
    }

    // 카테고리로 종목 찾기
    @GetMapping("/category")
    public List<Stock> getStocksByCategory(
            @RequestBody String category
    ) {
        return stockService.findByCategory(category);
    }
}
