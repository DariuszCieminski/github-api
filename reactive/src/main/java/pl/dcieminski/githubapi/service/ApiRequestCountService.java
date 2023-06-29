package pl.dcieminski.githubapi.service;

import reactor.core.publisher.Mono;

public interface ApiRequestCountService {

    Mono<Boolean> saveApiRequestCountForLogin(String login);
}