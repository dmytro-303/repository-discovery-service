package com.redcare.task.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Table("repository_info")
public record RepositoryInfo(
        @Id
        UUID id,
        String htmlUrl,
        Integer stargazersCount,
        LocalDate createdDate,
        String language
) {
}