package com.redcare.task.domain.repository;

import com.redcare.task.domain.RepositoryInfo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Repository
public interface RepositoryInfoRepository extends ReactiveCrudRepository<RepositoryInfo, Long> {

    @Query("SELECT * FROM repository_info WHERE created_date >= :startDate ORDER BY stargazers_count DESC LIMIT :limit")
    Flux<RepositoryInfo> findByCreatedAtAfter(LocalDate startDate, int limit);

    @Query("SELECT * FROM repository_info WHERE created_date >= :startDate and language = :language ORDER BY stargazers_count DESC LIMIT :limit")
    Flux<RepositoryInfo> findByLanguageAndCreatedAtAfter(String language, LocalDate startDate, int limit);
}
