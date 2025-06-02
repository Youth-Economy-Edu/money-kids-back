package com.moneykidsback.repository;

import com.moneykidsback.model.entity.StockPriceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockPriceLogRepository extends JpaRepository<StockPriceLog, Integer> {
} 