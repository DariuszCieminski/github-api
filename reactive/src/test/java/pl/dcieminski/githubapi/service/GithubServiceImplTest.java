package pl.dcieminski.githubapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pl.dcieminski.githubapi.model.GithubUser;
import pl.dcieminski.githubapi.util.TestData;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static reactor.test.StepVerifier.create;

@SpringBootTest
@MockitoSettings
class GithubServiceImplTest {

    @SpyBean
    private ApiRequestCountService apiRequestCountService;

    @Autowired
    private ObjectMapper mapper;

    private final MockWebServer mockWebServer = new MockWebServer();

    private GithubServiceImpl githubService;

    @BeforeEach
    void setup() {
        this.githubService = new GithubServiceImpl(apiRequestCountService, this.mockWebServer.url("").toString());
    }

    @Test
    void should_returnGithubUserAndSaveRequestCount_when_validLoginIsProvided() throws Exception {
        GithubUser testUser = TestData.getGithubUser();
        Predicate<GithubUser> responseBodyPredicate = response -> response != null && testUser.getLogin().equals(response.getLogin())
                && testUser.getId().equals(response.getId()) && testUser.getCreatedAt().equals(response.getCreatedAt());
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setBody(mapper.writeValueAsString(testUser)));

        create(githubService.getGithubUserByLogin(testUser.getLogin()))
                .expectNextMatches(responseBodyPredicate)
                .verifyComplete();
        verify(apiRequestCountService).saveApiRequestCountForLogin(testUser.getLogin());
    }

    @Test
    void should_notReturnGithubUserAndNotCallApiRequestCountService_when_invalidLoginIsProvided() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.NOT_FOUND.value()));
        Predicate<Throwable> errorPredicate = error -> error instanceof WebClientResponseException
                && ((WebClientResponseException) error).getStatusCode().equals(HttpStatus.NOT_FOUND);

        create(githubService.getGithubUserByLogin("invalidLogin"))
                .expectErrorMatches(errorPredicate)
                .verify();
        verifyNoInteractions(apiRequestCountService);
    }

    @Test
    void should_notReturnGithubUserAndNotCallApiRequestCountService_when_responseIsNull() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));

        create(githubService.getGithubUserByLogin("login")).verifyComplete();
        verifyNoInteractions(apiRequestCountService);
    }

    @Test
    void should_notReturnGithubUserAndNotCallApiRequestCountService_when_timeoutOccurs() throws Exception {
        mockWebServer.enqueue(new MockResponse().setBodyDelay(11, TimeUnit.SECONDS)
                                                .setBody(mapper.writeValueAsString(TestData.getGithubUser())));

        create(githubService.getGithubUserByLogin("login"))
                .expectErrorMatches(error -> error instanceof TimeoutException)
                .verify();
        verifyNoInteractions(apiRequestCountService);
    }
}