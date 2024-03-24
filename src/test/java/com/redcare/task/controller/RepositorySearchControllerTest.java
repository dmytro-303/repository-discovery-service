package com.redcare.task.controller;

import com.redcare.task.RepositorySearchService;
import com.redcare.task.domain.RepositoryInfo;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(RepositorySearchController.class)
public class RepositorySearchControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private RepositorySearchService repositorySearchService;

    @Test
    public void fetchRepositories_ReturnsRepositoryInfo() {
        var mockResponse = new RepositoryInfo(
                null, "url", 100, LocalDate.now(), "Java"
        );
        when(repositorySearchService.findTopRepositories(any(LocalDate.class), eq("Java"), eq(5)))
                .thenAnswer((Answer<Flux<RepositoryInfo>>) invocationOnMock ->
                        Flux.just(mockResponse));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/repositories")
                        .queryParam("startDate", "2023-01-01")
                        .queryParam("language", "Java")
                        .queryParam("limit", 5)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].htmlUrl").isEqualTo("url")
                .jsonPath("$[0].stargazersCount").isEqualTo(100)
                .jsonPath("$[0].createdAt").isNotEmpty()
                .jsonPath("$[0].language").isEqualTo("Java");
    }
}
