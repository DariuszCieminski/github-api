package pl.dcieminski.githubapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import pl.dcieminski.githubapi.model.GithubUser;
import pl.dcieminski.githubapi.util.TestData;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@MockitoSettings
class GithubServiceImplTest {

    @SpyBean
    private ApiRequestCountService apiRequestCountService;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Autowired
    private ObjectMapper mapper;

    private final MockWebServer mockWebServer = new MockWebServer();

    private GithubServiceImpl githubService;

    @BeforeEach
    void setup() {
        this.githubService = new GithubServiceImpl(restTemplateBuilder, apiRequestCountService,
                this.mockWebServer.url("").toString());
    }

    @Test
    void should_returnGithubUserAndSaveRequestCount_when_validLoginIsProvided() throws Exception {
        GithubUser testUser = TestData.getGithubUser();
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setBody(mapper.writeValueAsString(testUser)));

        GithubUser userFromApi = githubService.getGithubUserByLogin(testUser.getLogin());

        verify(apiRequestCountService).saveApiRequestCountForLogin(testUser.getLogin());
        assertNotNull(userFromApi);
        assertEquals(testUser.getLogin(), userFromApi.getLogin());
        assertEquals(testUser.getId(), userFromApi.getId());
        assertEquals(testUser.getCreatedAt(), userFromApi.getCreatedAt());
        assertEquals(testUser.getType(), userFromApi.getType());
        assertEquals(testUser.getName(), userFromApi.getName());
        assertEquals(testUser.getAvatarUrl(), userFromApi.getAvatarUrl());
        assertEquals(testUser.getFollowers(), userFromApi.getFollowers());
        assertEquals(testUser.getPublicRepos(), userFromApi.getPublicRepos());
    }

    @Test
    void should_notReturnGithubUserAndNotCallApiRequestCountService_when_invalidLoginIsProvided() throws Exception {
        String responseBody = mapper.writeValueAsString(Collections.singletonMap("error", "Not found"));
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.NOT_FOUND.value())
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setBody(responseBody));

        HttpStatusCodeException thrownException = assertThrows(HttpStatusCodeException.class, () -> githubService.getGithubUserByLogin("invalidLogin"));

        verifyNoInteractions(apiRequestCountService);
        assertEquals(HttpStatus.NOT_FOUND, thrownException.getStatusCode());
        assertEquals(responseBody, thrownException.getResponseBodyAsString());
    }

    @Test
    void should_notReturnGithubUserAndNotCallApiRequestCountService_when_responseIsNull() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));

        GithubUser userFromApi = githubService.getGithubUserByLogin("login");

        verifyNoInteractions(apiRequestCountService);
        assertNull(userFromApi);
    }

    @Test
    void should_notReturnGithubUserAndNotCallApiRequestCountService_when_timeoutOccurs() throws Exception {
        mockWebServer.enqueue(new MockResponse().setBodyDelay(11, TimeUnit.SECONDS)
                                                .setBody(mapper.writeValueAsString(TestData.getGithubUser())));

        assertThrows(ResourceAccessException.class, () -> githubService.getGithubUserByLogin("login"));
        verifyNoInteractions(apiRequestCountService);
    }
}