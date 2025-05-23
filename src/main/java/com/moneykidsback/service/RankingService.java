package com.moneykidsback.service;

import com.moneykidsback.dto.request.StockInfoDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Service;


@Service
public class RankingService {

    @MessageMapping("/stock-info") //클라이언트(주가변동로직) 가 보낸 데이터 수신
    public void receiveStockMessage(StockInfoDto stockInfo) {
        // 전달받은 데이터 처리 로직 작성
        System.out.println("받은 데이터: " + stockInfo.getCode() + ", " + stockInfo.getName() + ", " + stockInfo.getPrice() + ", " + stockInfo.getCategory());
        // 이후 필요한 비즈니스 로직 수행
    }
}
