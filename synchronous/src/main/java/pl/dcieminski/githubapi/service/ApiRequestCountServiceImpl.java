package pl.dcieminski.githubapi.service;

import org.springframework.stereotype.Service;
import pl.dcieminski.githubapi.model.ApiRequestCount;
import pl.dcieminski.githubapi.repository.ApiRequestCountRepository;

@Service
public class ApiRequestCountServiceImpl implements ApiRequestCountService {

    private final ApiRequestCountRepository apiRequestCountRepository;

    public ApiRequestCountServiceImpl(ApiRequestCountRepository apiRequestCountRepository) {
        this.apiRequestCountRepository = apiRequestCountRepository;
    }

    @Override
    public void saveApiRequestCountForLogin(String login) {
        if (apiRequestCountRepository.existsByLogin(login)) {
            apiRequestCountRepository.increaseRequestCountForLogin(login);
        } else {
            apiRequestCountRepository.save(new ApiRequestCount(login, 1));
        }
    }
}