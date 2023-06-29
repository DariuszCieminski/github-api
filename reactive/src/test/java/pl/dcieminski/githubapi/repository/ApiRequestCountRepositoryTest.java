package pl.dcieminski.githubapi.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import pl.dcieminski.githubapi.model.ApiRequestCount;
import pl.dcieminski.githubapi.util.TestData;

import java.util.function.Predicate;

import static reactor.test.StepVerifier.create;

@DataMongoTest
class ApiRequestCountRepositoryTest {

    @Autowired
    private ApiRequestCountRepository apiRequestCountRepository;

    @Test
    void should_returnFalse_when_loginDoesNotExist() {
        create(apiRequestCountRepository.existsByLogin("NonExistentUser")).expectNext(false);
    }

    @Test
    void should_returnTrue_when_loginExists() {
        ApiRequestCount apiRequestCount = TestData.getApiRequestCount();
        Predicate<ApiRequestCount> predicate = saved -> apiRequestCount.getLogin().equals(saved.getLogin())
                && apiRequestCount.getRequestCount().equals(saved.getRequestCount());

        create(apiRequestCountRepository.save(apiRequestCount)).expectNextMatches(predicate).verifyComplete();
        create(apiRequestCountRepository.existsByLogin(apiRequestCount.getLogin())).expectNext(true).verifyComplete();
    }

    @Test
    void should_increaseRequestCountForLogin_when_loginExists() {
        ApiRequestCount apiRequestCount = TestData.getApiRequestCount();
        Predicate<ApiRequestCount> savePredicate = saved -> apiRequestCount.getLogin().equals(saved.getLogin())
                && saved.getRequestCount().equals(apiRequestCount.getRequestCount());
        Predicate<ApiRequestCount> requestCountPredicate =
                foundById -> foundById.getRequestCount().equals(apiRequestCount.getRequestCount() + 1);

        create(apiRequestCountRepository.save(apiRequestCount)).expectNextMatches(savePredicate).verifyComplete();
        create(apiRequestCountRepository.increaseRequestCountForLogin(apiRequestCount.getLogin())).verifyComplete();
        create(apiRequestCountRepository.findById(apiRequestCount.getId())).expectNextMatches(requestCountPredicate).verifyComplete();
    }

    @Test
    void should_increaseRequestCountForSingleRecord_when_multipleRecordsAreInDatabase() {
        ApiRequestCount apiRequestCount1 = new ApiRequestCount("TestUser1", 1);
        ApiRequestCount apiRequestCount2 = new ApiRequestCount("TestUser2", 1);
        create(apiRequestCountRepository.save(apiRequestCount1)).expectNext(apiRequestCount1).verifyComplete();
        create(apiRequestCountRepository.save(apiRequestCount2)).expectNext(apiRequestCount2).verifyComplete();

        create(apiRequestCountRepository.increaseRequestCountForLogin(apiRequestCount1.getLogin())).verifyComplete();
        create(apiRequestCountRepository.findById(apiRequestCount1.getId()))
                .expectNextMatches(found -> found.getRequestCount().equals(apiRequestCount1.getRequestCount() + 1))
                .verifyComplete();
        create(apiRequestCountRepository.findById(apiRequestCount2.getId()))
                .expectNextMatches(found -> found.getRequestCount().equals(apiRequestCount2.getRequestCount()))
                .verifyComplete();
    }
}