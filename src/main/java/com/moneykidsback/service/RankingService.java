package com.moneykidsback.service;

import com.moneykidsback.dto.response.StockChangeRateDto;
import com.moneykidsback.model.Stock;
import com.moneykidsback.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


@Service
public class RankingService {

    private static final Logger logger = Logger.getLogger(RankingService.class.getName());


    @Autowired
    private StockRepository stockRepository;

    //주가 순위
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

    //카테고리별 순위
    public List<Stock> findAllByCategoryOrderByPriceDesc(String category)    {
        try{
            return stockRepository.findAllByCategoryOrderByPriceDesc(category);
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "RankingService 의 DB 접근 에러 발생", e);
            return Collections.emptyList(); // 빈 리스트 반환
        } catch (Exception e) {
            logger.log(Level.SEVERE, "DB 예외 발생", e);
            return Collections.emptyList(); // 빈 리스트 반환
        }
    }

    // 변동률 기준 순위
    // todo: 주식 테이블에 beforPrice 컬럼 필요함
    public List<Stock> getStocksOrderedByChangeRateDesc() {
        try {
            List<StockChangeRateDto> dtoList = stockRepository.findAllOrderByChangeRateDescWithDto();
            List<Stock> stocks = new ArrayList<>();
            for (StockChangeRateDto dto : dtoList) {
                Stock stock = new Stock();
                stock.setCode(dto.getCode());
                stock.setName(dto.getName());
                stock.setPrice(dto.getPrice());
                stock.setCategory(dto.getCategory());
                stock.setUpdatedAt(dto.getUpdateAt());
                // 필요하다면 beforePrice 등 추가 세팅
                stocks.add(stock);
            }
            return stocks;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "변동률 기준 주식 조회 실패", e);
            return Collections.emptyList();
        }
    }
}
