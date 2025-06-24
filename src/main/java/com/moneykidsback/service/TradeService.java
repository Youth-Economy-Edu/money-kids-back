package com.moneykidsback.service;

import com.moneykidsback.model.dto.request.TradeRequest;
import com.moneykidsback.model.dto.response.*;
import com.moneykidsback.model.entity.ActivityLog;
import com.moneykidsback.model.entity.Stock;
import com.moneykidsback.model.entity.StockLog;
import com.moneykidsback.model.entity.User;
import com.moneykidsback.model.entity.UserStock;
import com.moneykidsback.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final UserStockRepository userStockRepository;
    private final StockLogRepository stockLogRepository;
    private final ActivityLogRepository activityLogRepository;

    // ✅ 주문 가능 금액 및 투자 정보
    public BalanceResponse getBalance(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));
        
        int cashBalance = user.getPoints(); // 현금 잔고
        
        // 보유 주식 조회
        List<UserStock> userStocks = userStockRepository.findAllByUserId(userId);
        
        int totalStockValue = 0; // 현재 주식 평가금액
        int totalInvestment = 0; // 총 투자금액
        
        for (UserStock userStock : userStocks) {
            if (userStock.getQuantity() > 0) {
                Stock stock = userStock.getStock();
                if (stock != null) {
                    // 현재 주식 평가금액
                    totalStockValue += stock.getPrice() * userStock.getQuantity();
                    // 총 투자금액 (매수 시 투입한 금액)
                    totalInvestment += userStock.getTotal();
                }
            }
        }
        
        int totalAsset = cashBalance + totalStockValue; // 총 자산
        int profit = totalStockValue - totalInvestment; // 수익/손실
        double rate = totalInvestment > 0 ? ((double) profit / totalInvestment) * 100 : 0.0; // 수익률
        
        BalanceResponse response = new BalanceResponse();
        response.setBalance(cashBalance);
        response.setTotalAsset(totalAsset);
        response.setProfit(profit);
        response.setRate(rate);
        
        return response;
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
                .orElse(null);
        
        if (userStock == null) {
            // 새로운 UserStock 생성
            userStock = new UserStock(userId, request.getStockId(), 0, 0);
        }

        userStock.setQuantity(userStock.getQuantity() + request.getQuantity());
        userStock.setTotal(userStock.getTotal() + cost);
        userStockRepository.save(userStock);

        StockLog log = new StockLog(UUID.randomUUID().toString(), userId, request.getStockId(), LocalDateTime.now(), request.getQuantity());
        stockLogRepository.save(log);

        // ActivityLog 저장
        try {
            ActivityLog activityLog = ActivityLog.builder()
                    .userId(userId)
                    .activityType("TRADE")
                    .stocklogId(log.getId())
                    .stockCompanySize("대기업") // 실제로는 stock.getCompanySize() 등으로 설정
                    .stockCategory(stock.getCategory())
                    .status("SUCCESS")
                    .build();
            activityLogRepository.save(activityLog);
            System.out.println("✅ ActivityLog 저장 성공: " + activityLog.getId());
        } catch (Exception e) {
            System.err.println("❌ ActivityLog 저장 실패: " + e.getMessage());
            e.printStackTrace();
        }
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

        // 매도할 수량만큼 차감
        userStock.setQuantity(userStock.getQuantity() - request.getQuantity());
        
        // total은 평균 매수가 기준으로 비례해서 차감
        if (userStock.getQuantity() > 0) {
            // 남은 수량이 있으면 비례해서 total 차감
            int originalQuantity = userStock.getQuantity() + request.getQuantity();
            int newTotal = (userStock.getTotal() * userStock.getQuantity()) / originalQuantity;
            userStock.setTotal(newTotal);
            userStockRepository.save(userStock);
        } else {
            // 수량이 0이 되면 UserStock 삭제
            userStockRepository.delete(userStock);
        }

        StockLog log = new StockLog(UUID.randomUUID().toString(), userId, request.getStockId(), LocalDateTime.now(), -request.getQuantity());
        stockLogRepository.save(log);

        // ActivityLog 저장
        ActivityLog activityLog = ActivityLog.builder()
                .userId(userId)
                .activityType("TRADE")
                .stocklogId(log.getId())
                .stockCompanySize("대기업") // 실제로는 stock.getCompanySize() 등으로 설정
                .stockCategory(stock.getCategory())
                .status("SUCCESS")
                .build();
        activityLogRepository.save(activityLog);
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
                .map(log -> new TradeHistoryResponse(log.getStock().getId(), log.getDate().toString(), log.getQuantity()))
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
                .map(log -> new TradeHistoryResponse(log.getStock().getId(), log.getDate().toString(), log.getQuantity()))
                .collect(Collectors.toList());
    }

    // ✅ 포트폴리오 조회
    public Map<String, Object> getPortfolio(String userId) {
        Map<String, Object> portfolio = new HashMap<>();
        
        // 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));
        
        // 보유 주식 조회
        List<UserStock> userStocks = userStockRepository.findAllByUserId(userId);
        
        // 보유 주식을 Map 형태로 변환
        List<Map<String, Object>> stocks = userStocks.stream()
                .filter(userStock -> userStock.getQuantity() > 0) // 수량이 0보다 큰 것만
                .map(userStock -> {
                    Map<String, Object> stockInfo = new HashMap<>();
                    
                    // 주식 정보는 이미 연관관계로 로드됨
                    Stock stock = userStock.getStock();
                    
                    if (stock != null) {
                        stockInfo.put("stockName", stock.getName());
                        stockInfo.put("stockId", stock.getId());
                        stockInfo.put("quantity", userStock.getQuantity());
                        stockInfo.put("totalValue", userStock.getTotal());
                        stockInfo.put("currentPrice", stock.getPrice());
                    }
                    
                    return stockInfo;
                })
                .collect(Collectors.toList());
        
        // 포트폴리오 정보 구성
        portfolio.put("userId", userId);
        portfolio.put("points", user.getPoints());
        portfolio.put("stocks", stocks);
        
        return portfolio;
    }
}
