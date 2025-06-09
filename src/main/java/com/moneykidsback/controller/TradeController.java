package com.moneykidsback.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody TradeRequest request) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 실패: 로그인 필요");
        }

        tradeService.buyStock(userDetails.getUsername(), request);
        return ResponseEntity.ok("매수 완료");
    }




    @PostMapping("/sell")
    public ResponseEntity<?> sellStock(@RequestBody TradeRequest request,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        tradeService.sellStock(userDetails.getUsername(), request);
        return ResponseEntity.ok().body("매도 완료");
    }


    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> getBalance(@AuthenticationPrincipal UserDetails userDetails) {
        BalanceResponse balance = tradeService.getBalance(userDetails.getUsername());
        return ResponseEntity.ok(balance);
    }


    @GetMapping("/order/{stockId}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(@AuthenticationPrincipal UserDetails userDetails,
                                                              @PathVariable String stockId) {
        return ResponseEntity.ok(tradeService.getOrderDetail(userDetails.getUsername(), stockId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<TradeHistoryResponse>> getAllTradeHistory(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(tradeService.getAllTradeHistory(userDetails.getUsername()));
    }

    @GetMapping("/history/{type}")
    public ResponseEntity<List<TradeHistoryResponse>> getTradeHistoryByType(@AuthenticationPrincipal UserDetails userDetails,
                                                                            @PathVariable String type) {
        return ResponseEntity.ok(tradeService.getTradeHistoryByType(userDetails.getUsername(), type));
    }
}