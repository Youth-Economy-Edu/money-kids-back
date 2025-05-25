package com.moneykidsback.repository;

import com.moneykidsback.model.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, String> {
    // 주식코드로 종목 찾기
    List<Stock> findByCode(String code);

    // 종목 이름으로 종목 찾기
    List<Stock> findByName(String name);

    // 카테고리로 종목 찾기
    List<Stock> findByCategory(String category);
}
