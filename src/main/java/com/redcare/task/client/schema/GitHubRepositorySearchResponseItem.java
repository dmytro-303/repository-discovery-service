package com.redcare.task.client.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.redcare.task.domain.RepositoryInfo;

import java.time.Instant;
import java.time.LocalDate;

import static java.time.ZoneOffset.UTC;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubRepositorySearchResponseItem(
        @JsonProperty("html_url") String htmlUrl,
        @JsonProperty("stargazers_count") Integer stargazersCount,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("language") String language) {

    public RepositoryInfo toRepository() {
        return new RepositoryInfo(null, htmlUrl, stargazersCount, LocalDate.ofInstant(createdAt, UTC), language);
    }
}