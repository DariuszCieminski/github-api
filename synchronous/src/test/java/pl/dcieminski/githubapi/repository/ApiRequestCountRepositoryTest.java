package pl.dcieminski.githubapi.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.dcieminski.githubapi.model.ApiRequestCount;
import pl.dcieminski.githubapi.util.TestData;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ApiRequestCountRepositoryTest {

    @Autowired
    private ApiRequestCountRepository apiRequestCountRepository;

    @Test
    void should_returnFalse_when_loginDoesNotExist() {
        assertFalse(apiRequestCountRepository.existsByLogin("NonExistentUser"));
    }

    @Test
    void should_returnTrue_when_loginExists() {
        ApiRequestCount apiRequestCount = TestData.getApiRequestCount();
        apiRequestCountRepository.save(apiRequestCount);

        assertTrue(apiRequestCountRepository.existsByLogin(apiRequestCount.getLogin()));
    }

    @Test
    void should_increaseRequestCountForLogin_when_loginExists() {
        ApiRequestCount apiRequestCount = TestData.getApiRequestCount();
        apiRequestCount.setId(null);
        apiRequestCountRepository.save(apiRequestCount);

        apiRequestCountRepository.increaseRequestCountForLogin(apiRequestCount.getLogin());

        Optional<ApiRequestCount> increasedCountOptional = apiRequestCountRepository.findById(apiRequestCount.getId());
        assertTrue(increasedCountOptional.isPresent());
        assertEquals(apiRequestCount.getRequestCount() + 1, increasedCountOptional.get().getRequestCount());
    }

    @Test
    void should_increaseRequestCountForSingleRecord_when_multipleRecordsAreInDatabase() {
        ApiRequestCount requestCount1 = new ApiRequestCount("TestUser1", 1);
        ApiRequestCount requestCount2 = new ApiRequestCount("TestUser2", 1);
        apiRequestCountRepository.save(requestCount1);
        apiRequestCountRepository.save(requestCount2);

        apiRequestCountRepository.increaseRequestCountForLogin(requestCount1.getLogin());

        Optional<ApiRequestCount> requestFoundById1 = apiRequestCountRepository.findById(requestCount1.getId());
        Optional<ApiRequestCount> requestFoundById2 = apiRequestCountRepository.findById(requestCount2.getId());
        assertTrue(requestFoundById1.isPresent());
        assertTrue(requestFoundById2.isPresent());
        assertEquals(requestCount1.getRequestCount() + 1, requestFoundById1.get().getRequestCount());
        assertEquals(requestCount2.getRequestCount(), requestFoundById2.get().getRequestCount());
    }
}