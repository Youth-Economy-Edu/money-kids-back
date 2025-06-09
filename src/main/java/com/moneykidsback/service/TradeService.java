package com.moneykidsback.service;

import com.moneykidsback.model.dto.request.TradeRequest;
import com.moneykidsback.model.dto.response.*;
import com.moneykidsback.model.entity.Stock;
import com.moneykidsback.model.entity.StockLog;
import com.moneykidsback.model.entity.User;
import com.moneykidsback.model.entity.UserStock;
import com.moneykidsback.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final UserStockRepository userStockRepository;
    private final StockLogRepository stockLogRepository;

    // ✅ 주문 가능 금액
    public BalanceResponse getBalance(String userId) {
        int points = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저 없음"))
                .getPoints();
        return new BalanceResponse(points);
    }

    // ✅ 매수
    @Transactional
    public void buyStock(String userId, TradeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));
        Stock stock = stockRepository.findById(request.getStockId())
                .orElseThrow(() -> new RuntimeException("종목 없음"));

        int cost = stock.getPrice() * request.getQuantity();
        if (user.getPoints() < cost) {
            throw new RuntimeException("포인트 부족");
        }

        user.setPoints(user.getPoints() - cost);
        userRepository.save(user);

        UserStock userStock = userStockRepository.findByUserIdAndStockId(userId, request.getStockId())
                .orElse(new UserStock(userId, request.getStockId(), 0, 0));

        userStock.setQuantity(userStock.getQuantity() + request.getQuantity());
        userStock.setTotal(userStock.getTotal() + cost);
        userStockRepository.save(userStock);

        StockLog log = new StockLog(UUID.randomUUID().toString(), userId, request.getStockId(), LocalDate.now().toString(), request.getQuantity());
        stockLogRepository.save(log);
    }

    // ✅ 매도
    @Transactional
    public void sellStock(String userId, TradeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));
        Stock stock = stockRepository.findById(request.getStockId())
                .orElseThrow(() -> new RuntimeException("종목 없음"));

        UserStock userStock = userStockRepository.findByUserIdAndStockId(userId, request.getStockId())
                .orElseThrow(() -> new RuntimeException("보유 종목 없음"));

        if (userStock.getQuantity() < request.getQuantity()) {
            throw new RuntimeException("보유 수량 부족");
        }

        int refund = stock.getPrice() * request.getQuantity();
        user.setPoints(user.getPoints() + refund);
        userRepository.save(user);

        userStock.setQuantity(userStock.getQuantity() - request.getQuantity());
        userStock.setTotal(userStock.getTotal() - refund);
        userStockRepository.save(userStock);

        StockLog log = new StockLog(UUID.randomUUID().toString(), userId, request.getStockId(), LocalDate.now().toString(), -request.getQuantity());
        stockLogRepository.save(log);
    }

    // ✅ 주문 상세 보기
    public OrderDetailResponse getOrderDetail(String userId, String stockId) {
        UserStock userStock = userStockRepository.findByUserIdAndStockId(userId, stockId)
                .orElseThrow(() -> new RuntimeException("보유 종목 없음"));
        return new OrderDetailResponse(stockId, userStock.getQuantity(), userStock.getTotal());
    }

    // ✅ 전체 거래 내역
    public List<TradeHistoryResponse> getAllTradeHistory(String userId) {
        return stockLogRepository.findByUserId(userId).stream()
                .map(log -> new TradeHistoryResponse(log.getStock().getId(), log.getDate(), log.getQuantity()))
                .collect(Collectors.toList());
    }

    // ✅ 체결 / 주문 구분 조회
    public List<TradeHistoryResponse> getTradeHistoryByType(String userId, String type) {
        return stockLogRepository.findByUserId(userId).stream()
                .filter(log -> {
                    if ("buy".equalsIgnoreCase(type)) {
                        return log.getQuantity() > 0;
                    } else if ("sell".equalsIgnoreCase(type)) {
                        return log.getQuantity() < 0;
                    }
                    return true;
                })
                .map(log -> new TradeHistoryResponse(log.getStock().getId(), log.getDate(), log.getQuantity()))
                .collect(Collectors.toList());
    }
}
