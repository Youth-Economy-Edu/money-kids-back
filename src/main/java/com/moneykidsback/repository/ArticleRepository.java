package com.moneykidsback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.moneykidsback.model.entity.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
}
