package com.moneykidsback.repository;

import com.moneykidsback.model.entity.StockLog;
import com.moneykidsback.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StockLogRepository extends JpaRepository<StockLog, String> {
    List<StockLog> findByUser(User user);

    // 매도 시점 이전의 매수 내역
    @Query("SELECT l FROM StockLog l WHERE l.user.id = :userId AND l.stock.id = :stockId AND l.date < :beforeDate AND l.quantity > 0")
    List<StockLog> findBuyLogsBefore(@Param("userId") String userId, @Param("stockId") String stockId, @Param("beforeDate") String beforeDate);
}