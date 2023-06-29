package pl.dcieminski.githubapi.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import pl.dcieminski.githubapi.dto.GithubUserDto;
import pl.dcieminski.githubapi.model.GithubUser;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class GithubUserMapper {

    public abstract GithubUserDto mapToGithubUserDto(GithubUser githubUser);

    @AfterMapping
    protected void setCalculationsFieldOnDto(GithubUser githubUser, @MappingTarget GithubUserDto dto) {
        if (areValuesInvalid(githubUser.getFollowers(), githubUser.getPublicRepos())) {
            dto.setCalculations(BigDecimal.ZERO);
        } else {
            dto.setCalculations(BigDecimal.valueOf(6.0 / githubUser.getFollowers() * (2.0 + githubUser.getPublicRepos()))
                                          .setScale(3, RoundingMode.HALF_EVEN));
        }
    }

    private static boolean areValuesInvalid(Integer followers, Integer publicRepos) {
        return followers == null || followers == 0 || publicRepos == null;
    }
}