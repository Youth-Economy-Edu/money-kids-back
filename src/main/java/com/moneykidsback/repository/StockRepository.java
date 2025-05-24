package com.moneykidsback.repository;

import com.moneykidsback.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, String> {

    //JPA 가 네이밍 규칙 따라 자동 생성한 쿼리메소드
    List<Stock> findAllByOrderByPriceDesc(); //가격 기준 주식 내림차순(순위) 메소드
}
