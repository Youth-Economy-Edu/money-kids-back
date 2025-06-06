package com.moneykidsback.repository;

import com.moneykidsback.model.entity.StockPriceLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockPriceLogRepository extends JpaRepository<StockPriceLog, Integer> {
    StockPriceLog findByStockIdAndDate(String stockId, String date);
    StockPriceLog findTopByStockIdOrderByDateDesc(String stockId);
}
