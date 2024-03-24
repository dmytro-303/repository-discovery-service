package com.redcare.task.controller.schema;

import com.redcare.task.domain.RepositoryInfo;

import java.time.LocalDate;

public record RepositoryInfoResponseItem(
        String htmlUrl, Integer stargazersCount, LocalDate createdAt, String language
) {

    public static RepositoryInfoResponseItem fromDomainModel(RepositoryInfo model) {
        return new RepositoryInfoResponseItem(model.htmlUrl(), model.stargazersCount(), model.createdDate(), model.language());
    }
}