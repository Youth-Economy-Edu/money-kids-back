package com.moneykidsback.repository;

import com.moneykidsback.model.entity.DailyQuest;
import com.moneykidsback.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface DailyQuestRepository extends JpaRepository<DailyQuest, Long> {
    List<DailyQuest> findByUserAndQuestDate(User user, LocalDate questDate);
}
