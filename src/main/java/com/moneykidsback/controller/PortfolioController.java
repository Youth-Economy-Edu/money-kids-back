package com.moneykidsback.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moneykidsback.model.dto.response.PortfolioResponseDTO;
import com.moneykidsback.service.PortfolioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 💰 자산 포트폴리오 컨트롤러
 * - 사용자별 보유 주식 현황
 * - 총 자산 가치 계산
 * - 수익률 분석
 */
@Tag(name = "Portfolio", description = "자산 포트폴리오 관리")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @Operation(summary = "포트폴리오 조회", description = "사용자의 자산 포트폴리오를 조회합니다")
    @GetMapping("/{userId}/portfolio")
    public ResponseEntity<PortfolioResponseDTO> getPortfolio(
            @Parameter(description = "사용자 ID", required = true) @PathVariable String userId) {
        return ResponseEntity.ok(portfolioService.getPortfolio(userId));
    }
}
