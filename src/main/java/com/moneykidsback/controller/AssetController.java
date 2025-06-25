package com.moneykidsback.controller;

import com.moneykidsback.model.dto.response.AssetPortfolioResponseDTO;
import com.moneykidsback.service.AssetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Asset", description = "자산 관리")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @Operation(summary = "사용자 자산 조회", description = "사용자의 자산 포트폴리오를 조회합니다")
    @GetMapping("/{userId}/asset")
    public AssetPortfolioResponseDTO getUserAsset(
            @Parameter(description = "사용자 ID", required = true) @PathVariable String userId) {
        return assetService.getAssetPortfolio(userId);
    }
}
