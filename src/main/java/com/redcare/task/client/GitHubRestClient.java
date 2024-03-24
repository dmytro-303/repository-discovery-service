package com.redcare.task.client;

import com.redcare.task.client.schema.GitHubRepositorySearchResponseItem;
import com.redcare.task.client.schema.GotHubRepositorySearchResponse;
import com.redcare.task.domain.RepositoryInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.regex.Pattern;

// TODO add test
@Component
public class GitHubRestClient {

    private final String baseUrl = "https://api.github.com";
    private final WebClient client = WebClient.create(baseUrl);

    private final String linkHeaderName = "Link";
    private final Pattern linkPattern = Pattern.compile("<([^>]+)>; rel=\"next\"");

    public Flux<RepositoryInfo> fetchRepositories(LocalDate createdDateStart) {
        return fetchPage("/search/repositories?q=created:>%s&sort=stars&order=desc&page=1".formatted(createdDateStart))
                .map(GitHubRepositorySearchResponseItem::toRepository);
    }

    private Flux<GitHubRepositorySearchResponseItem> fetchPage(String uri) {
        return client.get().uri(uri)
                .retrieve()
                .toEntity(GotHubRepositorySearchResponse.class)
                .flatMapMany(response -> {
                    var body = response.getBody();
                    if (body == null || body.items().isEmpty()) return Flux.empty();

                    var currentPage = Flux.fromIterable(response.getBody().items());
                    var nextLink = getNextLink(response.getHeaders().getFirst(linkHeaderName));
                    if (nextLink != null)
                        return currentPage.concatWith(fetchPage(nextLink));
                    else
                        return currentPage;
                });
    }

    private String getNextLink(String linkHeader) {
        if (linkHeader == null) {
            return null;
        }
        var matcher = linkPattern.matcher(linkHeader);
        if (matcher.find()) {
            return matcher.group(1).substring(baseUrl.length());
        }
        return null;
    }
}
