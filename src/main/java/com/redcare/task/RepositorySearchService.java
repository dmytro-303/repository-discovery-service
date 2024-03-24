package com.redcare.task;

import com.redcare.task.client.GitHubRestClient;
import com.redcare.task.domain.RepositoryInfo;
import com.redcare.task.domain.repository.RepositoryInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;


@Service
public class RepositorySearchService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${service.default-start-date}")
    private LocalDate defaultStartDate;

    @Value("${service.default-limit}")
    private Integer defaultLimit;

    @Value("${service.fetch-on-startup}")
    private Boolean fetchOnStartup;

    private final GitHubRestClient client;
    private final RepositoryInfoRepository repository;

    public RepositorySearchService(GitHubRestClient client, RepositoryInfoRepository repository) {
        this.client = client;
        this.repository = repository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void fetchRepositories() {
        if (fetchOnStartup) fetchAndSaveRepositories();
    }

    @Scheduled(cron = "0 0 0 * * ?") //run every night
    public void fetchAndSaveRepositories() {
        logger.info("Start updating repositories");
        repository.deleteAll().subscribe();
        repository.saveAll(client.fetchRepositories(defaultStartDate)).subscribe();
        logger.info("Finish updating repositories");
    }

    public Flux<RepositoryInfo> findTopRepositories(LocalDate startDate, String language, Integer limit) {
        var actualStartDate = startDate == null ? defaultStartDate : startDate;
        var actualLimit = limit == null ? defaultLimit : limit;
        if (language == null || language.isBlank())
            return repository.findByCreatedAtAfter(actualStartDate, actualLimit);
        else
            return repository.findByLanguageAndCreatedAtAfter(language, actualStartDate, actualLimit);
    }
}


