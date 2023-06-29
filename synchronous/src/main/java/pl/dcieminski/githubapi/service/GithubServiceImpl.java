package pl.dcieminski.githubapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import pl.dcieminski.githubapi.model.GithubUser;

import java.time.Duration;

@Service
public class GithubServiceImpl implements GithubService {

    private final RestTemplate restTemplate;
    private final ApiRequestCountService apiRequestCountService;

    public GithubServiceImpl(RestTemplateBuilder restTemplateBuilder, ApiRequestCountService apiRequestCountService, @Value("${github.api.url}") String githubApiUrl) {
        this.restTemplate = restTemplateBuilder.rootUri(githubApiUrl).setConnectTimeout(Duration.ofSeconds(10)).build();
        this.apiRequestCountService = apiRequestCountService;
    }

    @Override
    @Transactional
    public GithubUser getGithubUserByLogin(String login) {
        GithubUser githubUser = restTemplate.getForObject("/users/{login}", GithubUser.class, login);
        if (githubUser != null) {
            apiRequestCountService.saveApiRequestCountForLogin(login);
        }
        return githubUser;
    }
}