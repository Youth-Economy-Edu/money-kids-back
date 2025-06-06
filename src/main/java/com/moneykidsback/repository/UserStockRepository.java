package com.moneykidsback.repository;

import com.moneykidsback.model.User;
import com.moneykidsback.model.UserStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserStockRepository extends JpaRepository<UserStock, Long> {
    List<UserStock> findByUser(User user);
}