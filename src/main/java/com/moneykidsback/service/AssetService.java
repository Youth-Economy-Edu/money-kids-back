package com.moneykidsback.service;

import com.moneykidsback.model.dto.response.AssetPortfolioResponseDTO;
import com.moneykidsback.model.entity.StockPriceLog;
import com.moneykidsback.model.entity.UserStock;
import com.moneykidsback.model.entity.User;
import com.moneykidsback.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final UserRepository userRepository;
    private final UserStockRepository userStockRepository;
    private final StockPriceLogRepository stockPriceLogRepository;

    public AssetPortfolioResponseDTO getAssetPortfolio(String userId) {
        User user = userRepository.findById(userId).orElseThrow();

        List<UserStock> userStocks = userStockRepository.findByUser(user);

        List<AssetPortfolioResponseDTO.StockDTO> stocks = userStocks.stream().map(us -> {
            String stockId = us.getStock().getId();
            StockPriceLog latestPriceLog = stockPriceLogRepository.findTopByStockIdOrderByDateDesc(stockId);
            int currentPrice = latestPriceLog != null ? latestPriceLog.getVolatility() : 0;
            int totalValue = currentPrice * us.getQuantity();

            return AssetPortfolioResponseDTO.StockDTO.builder()
                    .stockName(us.getStock().getName())
                    .quantity(us.getQuantity())
                    .currentPrice(currentPrice)
                    .totalValue(totalValue)
                    .build();
        }).collect(Collectors.toList());

        int totalStockValue = stocks.stream().mapToInt(AssetPortfolioResponseDTO.StockDTO::getTotalValue).sum();
        int totalAsset = totalStockValue + user.getPoints();

        return AssetPortfolioResponseDTO.builder()
                .userId(user.getId())
                .name(user.getName())
                .points(user.getPoints())
                .totalAssetValue(totalAsset)
                .stocks(stocks)
                .build();
    }
} 
