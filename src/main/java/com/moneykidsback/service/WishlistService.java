package com.moneykidsback.service;

import com.moneykidsback.model.dto.request.SaveWishlistDto;
import com.moneykidsback.model.entity.Stock;
import com.moneykidsback.model.entity.User;
import com.moneykidsback.repository.StockRepository;
import com.moneykidsback.repository.UserRepository;
import com.moneykidsback.repository.WishlistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;

    public WishlistService(WishlistRepository wishlistRepository, UserRepository userRepository, StockRepository stockRepository) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
    }


    // 위시리스트에 주식 저장/삭제 (토글)
    public void saveWishlist(SaveWishlistDto saveWishlistDto) {
        User user = userRepository.findById(saveWishlistDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 userId의 사용자가 존재하지 않습니다."));
        Stock stock = stockRepository.findById(saveWishlistDto.getStockId())
                .orElseThrow(() -> new IllegalArgumentException("해당 stockId의 주식이 존재하지 않습니다."));

        if (wishlistRepository.findByUserIdAndStockId(user.getId(), stock.getId()) != null) {
            wishlistRepository.deleteByUserIdAndStockId(user.getId(), stock.getId());
        } else {
            wishlistRepository.insertWishlist(user.getId(), stock.getId());
        }
    }

    // 위시리스트 조회
    public List<Stock> getWishlistByUserId(String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 null이거나 0 이하일 수 없습니다.");
        }
        List<Stock> wishlist = wishlistRepository.findStocksByUserId(userId);
        if (wishlist == null || wishlist.isEmpty()) {
            return List.of(); // 빈 리스트 반환
        }
        return wishlist;
    }

    // 위시리스트에서 주식 삭제
//    public void deleteWishlist(SaveWishlistDto saveWishlistDto) {
//        Users userId = userRepository.findById(saveWishlistDto.getUserId())
//                .orElseThrow(() -> new IllegalArgumentException("해당 userId의 사용자가 존재하지 않습니다."));
//        Stock stockId = stockRepository.findById(saveWishlistDto.getStockId())
//                .orElseThrow(() -> new IllegalArgumentException("해당 stockId의 주식이 존재하지 않습니다."));
//        if (userId == null || stockId == null ) {
//            throw new IllegalArgumentException("userId와 stockId는 null일 수 없습니다.");
//        }
//        wishlistRepository.deleteByUserIdAndStockId(userId.getID(), stockId.getID());
//    }
}
