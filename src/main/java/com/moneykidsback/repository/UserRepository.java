package com.moneykidsback.repository;

import com.moneykidsback.model.dto.entity.StockLog;
import com.moneykidsback.model.dto.entity.User;
import com.moneykidsback.model.dto.entity.UserStock;
import com.moneykidsback.model.dto.entity.UserStockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}



