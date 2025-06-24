package com.moneykidsback.repository;

import com.moneykidsback.model.entity.StockPriceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockPriceLogRepository extends JpaRepository<StockPriceLog, Long> {
    StockPriceLog findByStockIdAndDate(String stockId, LocalDateTime date);
    StockPriceLog findTopByStockIdOrderByDateDesc(String stockId);
    List<StockPriceLog> findTop100ByStock_IdOrderByIdDesc(String stockId);
    
    // 주식 ID로 전체 로그를 날짜 내림차순으로 조회
    List<StockPriceLog> findByStock_IdOrderByDateDesc(String stockId);
}
