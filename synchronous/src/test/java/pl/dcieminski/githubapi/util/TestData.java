package pl.dcieminski.githubapi.util;

import pl.dcieminski.githubapi.model.ApiRequestCount;
import pl.dcieminski.githubapi.model.GithubUser;

import java.time.LocalDateTime;

public class TestData {

    public static GithubUser getGithubUser() {
        GithubUser githubUser = new GithubUser();
        githubUser.setId(123L);
        githubUser.setLogin("TestUser123");
        githubUser.setName("The Best GithubUser");
        githubUser.setType("User");
        githubUser.setAvatarUrl("https://avatars.githubusercontent.com/u/1234567");
        githubUser.setCreatedAt(LocalDateTime.of(2023, 6, 25, 18, 35, 41));
        githubUser.setFollowers(3);
        githubUser.setPublicRepos(2);
        return githubUser;
    }

    public static ApiRequestCount getApiRequestCount() {
        ApiRequestCount apiRequestCount = new ApiRequestCount("TestUser123", 5);
        apiRequestCount.setId(1L);
        return apiRequestCount;
    }
}