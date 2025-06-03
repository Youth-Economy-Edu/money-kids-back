package com.moneykidsback.model.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class PortfolioResponseDTO {
    private String name;
    private String investmentType;
    private int points;
    private List<StockDTO> stocks;
    private List<TradeLogDTO> tradeLogs;
    private double rating;

    @Data
    public static class StockDTO {
        private String stockName;
        private int quantity;
        private int totalValue;
    }

    @Data
    public static class TradeLogDTO {
        private String stockName;
        private String date;
        private int quantity;
        private String action; // "매수" or "매도"
    }
}
