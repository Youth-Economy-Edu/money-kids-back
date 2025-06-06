package com.moneykidsback.service;

import com.moneykidsback.model.*;
import com.moneykidsback.model.dto.response.PortfolioResponseDTO;
import com.moneykidsback.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final UserRepository userRepository;
    private final UserStockRepository userStockRepository;
    private final StockLogRepository stockLogRepository;
    private final StockPriceLogRepository stockPriceLogRepository;

    public PortfolioResponseDTO getPortfolio(String userId) {
        User user = userRepository.findById(userId).orElseThrow();

        // 1. 보유 주식 조회
        List<UserStock> userStocks = userStockRepository.findByUser(user);
        List<PortfolioResponseDTO.StockDTO> stockDTOs = userStocks.stream().map(us -> {
            PortfolioResponseDTO.StockDTO dto = new PortfolioResponseDTO.StockDTO();
            dto.setStockName(us.getStock().getName());
            dto.setQuantity(us.getQuantity());
            dto.setTotalValue(us.getTotal());
            return dto;
        }).collect(Collectors.toList());

        // 2. 거래 내역 조회
        List<StockLog> logs = stockLogRepository.findByUser(user);
        List<PortfolioResponseDTO.TradeLogDTO> tradeLogs = logs.stream().map(log -> {
            PortfolioResponseDTO.TradeLogDTO dto = new PortfolioResponseDTO.TradeLogDTO();
            dto.setStockName(log.getStock().getName());
            dto.setDate(log.getDate());
            dto.setQuantity(Math.abs(log.getQuantity()));
            dto.setAction(log.getQuantity() > 0 ? "매수" : "매도");
            return dto;
        }).collect(Collectors.toList());

        // 3. 현재가 정보 매핑
        Map<String, Integer> priceMap = new HashMap<>();
        logs.forEach(log -> {
            String stockId = log.getStock().getId();
            StockPriceLog spl = stockPriceLogRepository.findTopByStockIdOrderByDateDesc(stockId);
            if (spl != null) {
                priceMap.put(stockId, spl.getVolatility());
            }
        });

        // 4. 평점 계산 (수익 낸 횟수 / 전체 거래 횟수 * 5)
        double rating = calculateRating(logs, priceMap);

        // 5. 결과 조립
        PortfolioResponseDTO dto = new PortfolioResponseDTO();
        dto.setName(user.getName());
        dto.setInvestmentType(user.getTendency());
        dto.setPoints(user.getPoints());
        dto.setStocks(stockDTOs);
        dto.setTradeLogs(tradeLogs);
        dto.setRating(rating);
        return dto;
    }

    private double calculateRating(List<StockLog> logs, Map<String, Integer> currentPrices) {
        if (logs.isEmpty()) return 0.0;
        int total = 0, profit = 0;

        for (StockLog log : logs) {
            if (log.getQuantity() < 0) { // 매도
                int sellPrice = getPriceAtDate(log.getStock().getId(), log.getDate());
                int avgBuyPrice = getAverageBuyPrice(log.getUser().getId(), log.getStock().getId(), log.getDate());
                if (sellPrice > avgBuyPrice) profit++;
                total++;
            }
        }

        if (total == 0) return 0.0;
        return Math.round((double) profit / total * 5.0 * 10) / 10.0;
    }

    private int getPriceAtDate(String stockId, String date) {
        StockPriceLog log = stockPriceLogRepository.findByStockIdAndDate(stockId, date);
        return log != null ? log.getVolatility() : 0;
    }

    private int getAverageBuyPrice(String userId, String stockId, String beforeDate) {
        List<StockLog> buys = stockLogRepository.findBuyLogsBefore(userId, stockId, beforeDate);
        int total = 0, qty = 0;
        for (StockLog buy : buys) {
            int price = getPriceAtDate(stockId, buy.getDate());
            total += price * buy.getQuantity();
            qty += buy.getQuantity();
        }
        return qty == 0 ? 0 : total / qty;
    }
}
