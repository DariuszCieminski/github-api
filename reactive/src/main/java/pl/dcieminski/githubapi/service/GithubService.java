package pl.dcieminski.githubapi.service;

import pl.dcieminski.githubapi.model.GithubUser;
import reactor.core.publisher.Mono;

public interface GithubService {

    Mono<GithubUser> getGithubUserByLogin(String login);
}