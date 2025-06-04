package com.moneykidsback.model.dto.response;

import lombok.*;
import java.util.List;

@Data
@Builder
public class AssetPortfolioResponseDTO {
    private String userId;
    private String name;
    private int points;
    private int totalAssetValue;
    private List<StockDTO> stocks;

    @Data
    @Builder
    public static class StockDTO {
        private String stockName;
        private int quantity;
        private int currentPrice;
        private int totalValue;
    }
}