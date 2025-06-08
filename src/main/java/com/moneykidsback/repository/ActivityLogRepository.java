package com.moneykidsback.repository;

import com.moneykidsback.model.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findTop30ByUserIdOrderByCreatedAtDesc(String userId);
    Page<ActivityLog> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
}
