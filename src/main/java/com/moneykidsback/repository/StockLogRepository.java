package com.moneykidsback.repository;

import com.moneykidsback.model.entity.StockLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StockLogRepository extends JpaRepository<StockLog, String> {
    List<StockLog> findByUserId(String userId);
}

