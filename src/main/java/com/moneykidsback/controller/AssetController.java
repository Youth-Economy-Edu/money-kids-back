package com.moneykidsback.controller;

import com.moneykidsback.model.dto.response.AssetPortfolioResponseDTO;
import com.moneykidsback.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @GetMapping("/{userId}/asset")
    public AssetPortfolioResponseDTO getUserAsset(@PathVariable String userId) {
        return assetService.getAssetPortfolio(userId);
    }
}
