package pl.dcieminski.githubapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.dcieminski.githubapi.model.GithubUser;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@Service
public class GithubServiceImpl implements GithubService {

    private final WebClient webClient;
    private final ApiRequestCountService apiRequestCountService;

    public GithubServiceImpl(ApiRequestCountService apiRequestCountService, @Value("${github.api.url}") String githubApiUrl) {
        this.webClient = WebClient.create(githubApiUrl);
        this.apiRequestCountService = apiRequestCountService;
    }

    @Override
    public Mono<GithubUser> getGithubUserByLogin(String login) {
        return webClient.get().uri("/users/{login}", login)
                        .retrieve()
                        .bodyToMono(GithubUser.class)
                        .subscribeOn(Schedulers.boundedElastic())
                        .timeout(Duration.ofSeconds(10))
                        .flatMap(githubUser -> apiRequestCountService.saveApiRequestCountForLogin(login).thenReturn(githubUser));
    }
}