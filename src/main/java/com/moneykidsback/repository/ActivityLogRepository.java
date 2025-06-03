package com.moneykidsback.repository;
// activity_log 테이블과 연동되는 Repository
import com.moneykidsback.model.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// 특정 사용자의 최근 활동 로그 30개를 시간순 정렬하여 가져오는 쿼리
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findTop30ByUserIdOrderByCreatedAtDesc(String userId);
}
