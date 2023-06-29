package pl.dcieminski.githubapi.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.dcieminski.githubapi.dto.GithubUserDto;
import pl.dcieminski.githubapi.model.GithubUser;
import pl.dcieminski.githubapi.util.TestData;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringJUnitConfig(classes = GithubUserMapperImpl.class)
class GithubUserMapperTest {

    @Autowired
    private GithubUserMapper githubUserMapper;

    @Test
    void should_mapToGithubUserDto_when_githubUserIsNotNull() {
        GithubUser githubUser = TestData.getGithubUser();

        GithubUserDto dto = githubUserMapper.mapToGithubUserDto(githubUser);

        assertEquals(githubUser.getId(), dto.getId());
        assertEquals(githubUser.getLogin(), dto.getLogin());
        assertEquals(githubUser.getName(), dto.getName());
        assertEquals(githubUser.getType(), dto.getType());
        assertEquals(githubUser.getAvatarUrl(), dto.getAvatarUrl());
        assertEquals(githubUser.getCreatedAt(), dto.getCreatedAt());
    }

    @Test
    void should_returnNull_when_githubUserIsNull() {
        assertNull(githubUserMapper.mapToGithubUserDto(null));
    }

    @Test
    void should_setCalculationsFieldOnDto_when_GithubUserHasCorrectFollowersAndPublicReposFields() {
        GithubUser githubUser = TestData.getGithubUser();
        GithubUserDto dto = new GithubUserDto();

        githubUserMapper.setCalculationsFieldOnDto(githubUser, dto);

        assertEquals(new BigDecimal("8.000"), dto.getCalculations());
    }

    @ParameterizedTest
    @CsvSource(value = {"0,0", "0,1", "null,null", "null,1", "1,null"}, nullValues = "null")
    void should_setCalculationsFieldToZero_when_followersOrPublicReposAreNullOrZero(Integer followers, Integer publicRepos) {
        GithubUser githubUser = TestData.getGithubUser();
        githubUser.setFollowers(followers);
        githubUser.setPublicRepos(publicRepos);

        GithubUserDto dto = githubUserMapper.mapToGithubUserDto(githubUser);

        assertEquals(BigDecimal.ZERO, dto.getCalculations());
    }
}