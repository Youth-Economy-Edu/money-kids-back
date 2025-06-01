package com.moneykidsback.repository;

import com.moneykidsback.model.dto.request.NewSaveDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.moneykidsback.model.entity.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Article save(NewSaveDto article);
}
