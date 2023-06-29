package pl.dcieminski.githubapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.dcieminski.githubapi.dto.GithubUserDto;
import pl.dcieminski.githubapi.mapper.GithubUserMapper;
import pl.dcieminski.githubapi.model.GithubUser;
import pl.dcieminski.githubapi.service.GithubService;

@RestController
public class GithubApiController {

    private final GithubService githubService;
    private final GithubUserMapper githubUserMapper;

    public GithubApiController(GithubService githubService, GithubUserMapper githubUserMapper) {
        this.githubService = githubService;
        this.githubUserMapper = githubUserMapper;
    }

    @GetMapping("/users/{login}")
    public GithubUserDto getGithubUserInfoByLogin(@PathVariable String login) {
        GithubUser githubUser = githubService.getGithubUserByLogin(login);
        return githubUserMapper.mapToGithubUserDto(githubUser);
    }
}