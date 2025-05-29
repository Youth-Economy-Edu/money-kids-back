package com.moneykidsback.repository;

import com.moneykidsback.model.dto.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, String> {}