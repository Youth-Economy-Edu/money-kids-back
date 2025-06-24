package com.moneykidsback.repository;

import com.moneykidsback.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    // 기본적으로 findById(id) 등 자동 지원됨
    Optional<User> findByEmail(String email);
}

