package pl.dcieminski.githubapi.service;

import org.springframework.stereotype.Service;
import pl.dcieminski.githubapi.model.ApiRequestCount;
import pl.dcieminski.githubapi.repository.ApiRequestCountRepository;
import reactor.core.publisher.Mono;

@Service
public class ApiRequestCountServiceImpl implements ApiRequestCountService {

    private final ApiRequestCountRepository apiRequestCountRepository;

    public ApiRequestCountServiceImpl(ApiRequestCountRepository apiRequestCountRepository) {
        this.apiRequestCountRepository = apiRequestCountRepository;
    }

    @Override
    public Mono<Boolean> saveApiRequestCountForLogin(String login) {
        return apiRequestCountRepository.existsByLogin(login).flatMap(loginExists -> {
            if (loginExists) {
                return apiRequestCountRepository.increaseRequestCountForLogin(login).thenReturn(true);
            } else {
                return apiRequestCountRepository.save(new ApiRequestCount(login, 1)).thenReturn(false);
            }
        });
    }
}