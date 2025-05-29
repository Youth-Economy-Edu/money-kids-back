package com.moneykidsback.service;

import com.moneykidsback.model.entity.Stock;
import com.moneykidsback.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    @Autowired
    StockRepository stockRepository;

    public List<Stock> findByCode(String code) {
        return stockRepository.findByCode(code);
    }

    public List<Stock> findByName(String name) {
        return stockRepository.findByName(name);
    }

    public List<Stock> findByCategory(String category) {
        return stockRepository.findByCategory(category);
    }

    public Stock updateStock(Stock stock) {
        return stockRepository.save(stock);
    }
}
