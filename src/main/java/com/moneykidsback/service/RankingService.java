package com.moneykidsback.service;

import com.moneykidsback.model.Stock;
import com.moneykidsback.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


@Service
public class RankingService {

    private static final Logger logger = Logger.getLogger(RankingService.class.getName());


    @Autowired
    private StockRepository stockRepository;

    public List<Stock> getStocksOrderedByPriceDesc() {
        try {
            return stockRepository.findAllByOrderByPriceDesc();
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "RankingService 의 DB 접근 에러 발생", e);
            return Collections.emptyList(); // 빈 리스트 반환
        } catch (Exception e) {
            logger.log(Level.SEVERE, "DB 예외 발생", e);
            return Collections.emptyList(); // 빈 리스트 반환
        }
    }
}
