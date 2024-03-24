package com.redcare.task.controller;

import com.redcare.task.RepositorySearchService;
import com.redcare.task.controller.schema.RepositoryInfoResponseItem;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@RestController
@RequestMapping("/repositories")
public class RepositorySearchController {

    private final RepositorySearchService repositorySearchService;

    public RepositorySearchController(RepositorySearchService repositorySearchService) {
        this.repositorySearchService = repositorySearchService;
    }

    @GetMapping()
    public Flux<RepositoryInfoResponseItem> fetchRepositories(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Integer limit) {
        return repositorySearchService.findTopRepositories(startDate, language, limit).map(RepositoryInfoResponseItem::fromDomainModel);
    }
}
