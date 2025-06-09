package com.moneykidsback.repository;

import com.moneykidsback.model.entity.Stock;
import com.moneykidsback.model.entity.Wishlist;
import com.moneykidsback.model.entity.UserFavoriteStockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, UserFavoriteStockId> {

    // 위시리스트에 주식 추가
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO Wishlist (user_id, stock_id) VALUES (:userId, :stockId)", nativeQuery = true)
    void insertWishlist(@Param("userId") String userId, @Param("stockId") String stockId);

    // 사용자id로 위시리스트 전체 조회
    @Query("SELECT w.stockID FROM Wishlist w WHERE w.id.userId = :userId")
    List<Stock> findStocksByUserId(@Param("userId") String userId);

    //위시리스트에 특정 주식 조회(비즈니스로직 용도)
    @Query("SELECT w FROM Wishlist w WHERE w.id.userId = :userId AND w.id.stockId = :stockId")
    Wishlist findByUserIdAndStockId(@Param("userId") String userId, @Param("stockId") String stockId);

    // 위시리스트에서 주식 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM Wishlist w WHERE w.id.userId = :userId AND w.id.stockId = :stockId")
    void deleteByUserIdAndStockId(@Param("userId") String userId, @Param("stockId") String stockId);
}
