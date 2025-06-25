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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 💸 주식 거래 컨트롤러  
 * - 주식 매수/매도 기능
 * - 보유 주식 및 잔고 조회
 * - 거래 내역 및 수익률 분석
 * - 포트폴리오 관리
 */
@Tag(name = "Trading", description = "주식 거래 관리")
@RestController
@RequestMapping("/api/stocks/trade")
@RequiredArgsConstructor
public class TradeController {
    private final TradeService tradeService;

    @Operation(summary = "주식 매수", description = "주식을 매수합니다")
    @PostMapping("/buy")
    public ResponseEntity<String> buyStock(
            @RequestBody TradeRequest request,
            @Parameter(description = "사용자 ID") @RequestParam(value = "userId", required = false) String userId) {

        // userId가 없으면 기본값 사용 (개발/테스트용)
        if (userId == null || userId.isEmpty()) {
            userId = "master";
        }
        
        tradeService.buyStock(userId, request);
        return ResponseEntity.ok("매수 완료");
    }

    @Operation(summary = "주식 매도", description = "주식을 매도합니다")
    @PostMapping("/sell")
    public ResponseEntity<?> sellStock(
            @RequestBody TradeRequest request,
            @Parameter(description = "사용자 ID") @RequestParam(value = "userId", required = false) String userId) {
        
        // userId가 없으면 기본값 사용 (개발/테스트용)
        if (userId == null || userId.isEmpty()) {
            userId = "master";
        }
        
        tradeService.sellStock(userId, request);
        return ResponseEntity.ok().body("매도 완료");
    }

    @Operation(summary = "잔고 조회", description = "사용자의 현재 잔고를 조회합니다")
    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> getBalance(
            @Parameter(description = "사용자 ID") @RequestParam(value = "userId", defaultValue = "master") String userId) {
        BalanceResponse balance = tradeService.getBalance(userId);
        return ResponseEntity.ok(balance);
    }

    @Operation(summary = "포트폴리오 조회", description = "사용자의 포트폴리오를 조회합니다")
    @GetMapping("/portfolio")
    public ResponseEntity<Map<String, Object>> getPortfolio(
            @Parameter(description = "사용자 ID") @RequestParam(value = "userId", defaultValue = "master") String userId) {
        Map<String, Object> portfolio = tradeService.getPortfolio(userId);
        return ResponseEntity.ok(portfolio);
    }

    @Operation(summary = "주문 상세 조회", description = "특정 주식의 주문 상세 정보를 조회합니다")
    @GetMapping("/order/{stockId}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(
            @Parameter(description = "주식 ID", required = true) @PathVariable String stockId,
            @Parameter(description = "사용자 ID") @RequestParam(value = "userId", defaultValue = "master") String userId) {
        return ResponseEntity.ok(tradeService.getOrderDetail(userId, stockId));
    }

    @Operation(summary = "전체 거래 내역 조회", description = "사용자의 모든 거래 내역을 조회합니다")
    @GetMapping("/history")
    public ResponseEntity<List<TradeHistoryResponse>> getAllTradeHistory(
            @Parameter(description = "사용자 ID") @RequestParam(value = "userId", defaultValue = "master") String userId) {
        return ResponseEntity.ok(tradeService.getAllTradeHistory(userId));
    }

    @Operation(summary = "거래 타입별 내역 조회", description = "거래 타입별로 거래 내역을 조회합니다")
    @GetMapping("/history/{type}")
    public ResponseEntity<List<TradeHistoryResponse>> getTradeHistoryByType(
            @Parameter(description = "거래 타입", required = true) @PathVariable String type,
            @Parameter(description = "사용자 ID") @RequestParam(value = "userId", defaultValue = "master") String userId) {
        return ResponseEntity.ok(tradeService.getTradeHistoryByType(userId, type));
    }
}