package com.moneykidsback.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.moneykidsback.model.entity.ActivityLog;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findTop30ByUserIdOrderByCreatedAtDesc(String userId);
    Page<ActivityLog> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    
    // 학부모 페이지용 추가 메서드
    List<ActivityLog> findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(String userId, LocalDateTime after);
}
