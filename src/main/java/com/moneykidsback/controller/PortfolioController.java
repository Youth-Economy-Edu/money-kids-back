package com.moneykidsback.controller;

import com.moneykidsback.model.dto.response.PortfolioResponseDTO;
import com.moneykidsback.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping("/{userId}/portfolio")
    public ResponseEntity<PortfolioResponseDTO> getPortfolio(@PathVariable String userId) {
        return ResponseEntity.ok(portfolioService.getPortfolio(userId));
    }
}
