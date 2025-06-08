package com.moneykidsback.repository;

import com.moneykidsback.model.entity.User;
import com.moneykidsback.model.entity.UserStock;
import com.moneykidsback.model.entity.UserStockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStockRepository extends JpaRepository<UserStock, UserStockId> {
    Optional<UserStock> findByUserIdAndStockId(String userId, String stockId);
    List<UserStock> findAllByUserId(String userId);
    List<UserStock> findAllByUser(String userId);
}