package com.redcare.task.client.schema;

import java.util.List;

public record GotHubRepositorySearchResponse(List<GitHubRepositorySearchResponseItem> items) {
}