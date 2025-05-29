package com.moneykidsback.repository;

import com.moneykidsback.model.entity.TendencyAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.Optional;

// TendencyAnalysis 엔티티에 대한 CRUD 작업을 수행하는 JPA 레포지토리
@EnableJpaRepositories
public interface TendencyAnalysisRepository extends JpaRepository<TendencyAnalysis, String> {

    // TendencyAnalysis 엔티티에 대한 CRUD 메서드는 JpaRepository에서 제공됨
    // 추가적인 메서드가 필요하면 여기에 정의할 수 있음

    // 가장 최신 성향 분석 결과 가져오기 (userId 기준)
    Optional <TendencyAnalysis> findTopByUserIdOrderByCreatedAtDesc(String userId);

    // 특정 사용자의 성향 분석 결과를 생성일 기준으로 내림차순 정렬하여 가져오기
    List<TendencyAnalysis> findByUserIdOrderByCreatedAtDesc(String userId);

    // 특정 사용자의 성향 분석 결과 삭제
    void deleteByUserId(String userId);
    boolean existsByUserId(String userId);

    List<TendencyAnalysis> findAllByUserId(String userId);
}
