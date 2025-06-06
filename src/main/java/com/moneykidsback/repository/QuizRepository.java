package com.moneykidsback.repository;

import com.moneykidsback.model.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer> {

    @Query(value = "SELECT * FROM quiz WHERE level = :level ORDER BY RAND() LIMIT 5", nativeQuery = true)
    List<Quiz> findRandomQuizzesByLevel(@Param("level") String level);  // String으로 바꿈!

    boolean existsByQuestionAndLevel(String question, String level);

    List<Quiz> findByLevel(String level);
}
