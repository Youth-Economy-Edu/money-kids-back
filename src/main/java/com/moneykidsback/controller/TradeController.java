package com.moneykidsback.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moneykidsback.model.dto.request.TradeRequest;
import com.moneykidsback.model.dto.response.BalanceResponse;
import com.moneykidsback.model.dto.response.OrderDetailResponse;
import com.moneykidsback.model.dto.response.TradeHistoryResponse;
import com.moneykidsback.service.TradeService;

import lombok.RequiredArgsConstructor;

/**
 * 💸 주식 거래 컨트롤러  
 * - 주식 매수/매도 기능
 * - 보유 주식 및 잔고 조회
 * - 거래 내역 및 수익률 분석
 * - 포트폴리오 관리
 */
@RestController
@RequestMapping("/api/stocks/trade")
@RequiredArgsConstructor
public class TradeController {
    private final TradeService tradeService;

    @PostMapping("/buy")
    public ResponseEntity<String> buyStock(
            @RequestBody TradeRequest request,
            @RequestParam(value = "userId", required = false) String userId) {

        // userId가 없으면 기본값 사용 (개발/테스트용)
        if (userId == null || userId.isEmpty()) {
            userId = "master";
        }
        
        tradeService.buyStock(userId, request);
        return ResponseEntity.ok("매수 완료");
    }

    @PostMapping("/sell")
    public ResponseEntity<?> sellStock(
            @RequestBody TradeRequest request,
            @RequestParam(value = "userId", required = false) String userId) {
        
        // userId가 없으면 기본값 사용 (개발/테스트용)
        if (userId == null || userId.isEmpty()) {
            userId = "master";
        }
        
        tradeService.sellStock(userId, request);
        return ResponseEntity.ok().body("매도 완료");
    }

    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> getBalance(
            @RequestParam(value = "userId", defaultValue = "master") String userId) {
        BalanceResponse balance = tradeService.getBalance(userId);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/portfolio")
    public ResponseEntity<Map<String, Object>> getPortfolio(
            @RequestParam(value = "userId", defaultValue = "master") String userId) {
        Map<String, Object> portfolio = tradeService.getPortfolio(userId);
        return ResponseEntity.ok(portfolio);
    }

    @GetMapping("/order/{stockId}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(
            @PathVariable String stockId,
            @RequestParam(value = "userId", defaultValue = "master") String userId) {
        return ResponseEntity.ok(tradeService.getOrderDetail(userId, stockId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<TradeHistoryResponse>> getAllTradeHistory(
            @RequestParam(value = "userId", defaultValue = "master") String userId) {
        return ResponseEntity.ok(tradeService.getAllTradeHistory(userId));
    }

    @GetMapping("/history/{type}")
    public ResponseEntity<List<TradeHistoryResponse>> getTradeHistoryByType(
            @PathVariable String type,
            @RequestParam(value = "userId", defaultValue = "master") String userId) {
        return ResponseEntity.ok(tradeService.getTradeHistoryByType(userId, type));
    }
}