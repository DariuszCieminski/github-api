package pl.dcieminski.githubapi.service;

import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.dcieminski.githubapi.model.ApiRequestCount;
import pl.dcieminski.githubapi.repository.ApiRequestCountRepository;
import pl.dcieminski.githubapi.util.TestData;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static reactor.test.StepVerifier.create;

@SpringJUnitConfig(classes = ApiRequestCountServiceImpl.class)
@MockitoSettings
class ApiRequestCountServiceImplTest {

    @MockBean
    private ApiRequestCountRepository repository;

    @Autowired
    private ApiRequestCountServiceImpl service;

    @Test
    void should_increaseRequestCountForLogin_when_loginExists() {
        ApiRequestCount apiRequestCount = TestData.getApiRequestCount();
        when(repository.existsByLogin(apiRequestCount.getLogin())).thenReturn(Mono.just(true));
        when(repository.increaseRequestCountForLogin(apiRequestCount.getLogin())).thenReturn(Mono.empty());

        create(service.saveApiRequestCountForLogin(apiRequestCount.getLogin())).expectNext(true).verifyComplete();

        verify(repository, never()).save(any(ApiRequestCount.class));
        verify(repository).increaseRequestCountForLogin(apiRequestCount.getLogin());
    }

    @Test
    void should_saveNewApiRequestCountRecordToDatabase_when_loginDoesNotExist() {
        ApiRequestCount apiRequestCount = TestData.getApiRequestCount();
        when(repository.existsByLogin(apiRequestCount.getLogin())).thenReturn(Mono.just(false));
        when(repository.save(any(ApiRequestCount.class))).thenReturn(Mono.just(apiRequestCount));

        create(service.saveApiRequestCountForLogin(apiRequestCount.getLogin())).expectNext(false).verifyComplete();

        verify(repository).save(any(ApiRequestCount.class));
        verify(repository, never()).increaseRequestCountForLogin(apiRequestCount.getLogin());
    }
}