package com.redcare.task;

import com.redcare.task.client.GitHubRestClient;
import com.redcare.task.domain.RepositoryInfo;
import com.redcare.task.domain.repository.RepositoryInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.boot.test.mock.mockito.MockReset.AFTER;


@SpringBootTest(properties = {"service.fetch-on-startup=false", "service.default-limit=2"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureTestDatabase
public class RepositorySearchServiceIntegrationTest {

    static {
        PostgresContainer.startInstance();
    }

    @Autowired
    private RepositorySearchService repositorySearchService;

    @Autowired
    private RepositoryInfoRepository repositoryInfoRepository;

    @MockBean(reset = AFTER)
    private GitHubRestClient gitHubRestClient;

    @BeforeEach
    public void cleanup() {
        repositoryInfoRepository.deleteAll().subscribe();
    }

    @Test
    public void shouldDownloadRepositoriesInformation() {
        var repo = new RepositoryInfo(null, "htmlUrl", 2, LocalDate.now(), "java");

        Mockito.when(gitHubRestClient.fetchRepositories(any())).thenReturn(Flux.just(repo));

        repositorySearchService.fetchAndSaveRepositories();

        var found = repositoryInfoRepository.findAll().single().block();
        assertEquals(repo.createdDate(), found.createdDate());
        assertEquals(repo.htmlUrl(), found.htmlUrl());
        assertEquals(repo.language(), found.language());
        assertEquals(repo.stargazersCount(), found.stargazersCount());
    }

    @Test
    public void shouldFindTopRepositoryByLanguage() {
        var startDate = LocalDate.now();
        var language = "java";
        var limit = 1;
        var expected = new RepositoryInfo(null, "repo1", 100, startDate.plusDays(2), language);

        repositoryInfoRepository.saveAll(List.of(
                expected,
                new RepositoryInfo(null, "repo3", 99, startDate.plusDays(2), language),
                new RepositoryInfo(null, "repo4", 999, startDate.minusDays(1), language),
                new RepositoryInfo(null, "repo5", 200, startDate.plusDays(3), "kotlin")
        )).subscribe();

        var found = repositorySearchService.findTopRepositories(startDate, language, limit).single().block();

        assertEquals(expected.createdDate(), found.createdDate());
        assertEquals(expected.htmlUrl(), found.htmlUrl());
        assertEquals(expected.language(), found.language());
        assertEquals(expected.stargazersCount(), found.stargazersCount());
    }

    @Test
    public void shouldFindTopRepositoryByLanguageWithDefaultParams() {
        var startDate = LocalDate.now();
        var language = "java";
        var expected = new RepositoryInfo(null, "repo1", 100, startDate, language);

        repositoryInfoRepository.saveAll(List.of(
                expected,
                new RepositoryInfo(null, "repo2", 200, startDate, "kotlin")
        )).subscribe();

        var found = repositorySearchService.findTopRepositories(null, language, null).single().block();

        assertEquals(expected.createdDate(), found.createdDate());
        assertEquals(expected.htmlUrl(), found.htmlUrl());
        assertEquals(expected.language(), found.language());
        assertEquals(expected.stargazersCount(), found.stargazersCount());
    }

    @Test
    public void shouldFindRepositoryWithDefaultParams() {
        var startDate = LocalDate.now();
        var expected = new RepositoryInfo(null, "repo1", 100, startDate, "java");

        repositoryInfoRepository.save(expected).subscribe();

        repositorySearchService.findTopRepositories(null, null, null).single().block();
    }

}
