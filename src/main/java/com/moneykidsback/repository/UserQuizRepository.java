package com.moneykidsback.repository;

import com.moneykidsback.model.entity.UserQuiz;
import com.moneykidsback.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserQuizRepository extends JpaRepository<UserQuiz, Integer> {

    // 사용자 ID로 퀴즈 풀이 결과 전체 조회
    List<UserQuiz> findByUser(User user);
}
