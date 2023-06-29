package pl.dcieminski.githubapi.service;

import pl.dcieminski.githubapi.model.GithubUser;

public interface GithubService {

    GithubUser getGithubUserByLogin(String login);
}