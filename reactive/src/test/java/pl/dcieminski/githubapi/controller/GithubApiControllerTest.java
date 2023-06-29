package pl.dcieminski.githubapi.controller;

import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pl.dcieminski.githubapi.dto.GithubUserDto;
import pl.dcieminski.githubapi.mapper.GithubUserMapperImpl;
import pl.dcieminski.githubapi.model.GithubUser;
import pl.dcieminski.githubapi.service.GithubService;
import pl.dcieminski.githubapi.util.TestData;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(GithubApiController.class)
@Import(GithubUserMapperImpl.class)
@MockitoSettings
class GithubApiControllerTest {

    @MockBean
    private GithubService githubService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void should_ReturnOkWithCorrectResponseBody_when_ValidLoginIsProvided() {
        GithubUser githubUser = TestData.getGithubUser();
        when(githubService.getGithubUserByLogin(githubUser.getLogin())).thenReturn(Mono.just(githubUser));

        GithubUserDto responseBody = webTestClient.get().uri("/users/{login}", githubUser.getLogin()).exchange()
                                                  .expectStatus().isOk()
                                                  .expectHeader().contentType(MediaType.APPLICATION_JSON)
                                                  .expectBody(GithubUserDto.class)
                                                  .returnResult().getResponseBody();

        assertNotNull(responseBody);
        assertAll("Response body assertions",
                () -> assertEquals(githubUser.getId(), responseBody.getId()),
                () -> assertEquals(githubUser.getLogin(), responseBody.getLogin()),
                () -> assertEquals(githubUser.getName(), responseBody.getName()),
                () -> assertEquals(githubUser.getType(), responseBody.getType()),
                () -> assertEquals(githubUser.getAvatarUrl(), responseBody.getAvatarUrl()),
                () -> assertEquals(githubUser.getCreatedAt(), responseBody.getCreatedAt()),
                () -> assertEquals(new BigDecimal("8.000"), responseBody.getCalculations())
        );
    }

    @Test
    void should_Return404_when_LoginDoesNotExist() {
        WebClientResponseException exception = new WebClientResponseException(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.toString(), null, null, null);
        when(githubService.getGithubUserByLogin(anyString())).thenThrow(exception);

        webTestClient.get().uri("/users/{login}", "nonExistentLogin").exchange()
                     .expectStatus().isNotFound()
                     .expectHeader().contentType(MediaType.APPLICATION_JSON)
                     .expectBody().jsonPath("$.error").value(is(equalTo(exception.getStatusText())));
    }

    @Test
    void should_Return500_when_ConnectionErrorOccurs() {
        WebClientRequestException exception = new WebClientRequestException(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR), HttpMethod.GET, URI.create("/login/validLogin"), new HttpHeaders());
        when(githubService.getGithubUserByLogin(anyString())).thenThrow(exception);

        webTestClient.get().uri("/users/{login}", "validLogin").exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectHeader().contentType(MediaType.APPLICATION_JSON)
                     .expectBody().jsonPath("$.error").value(is(equalTo(exception.getMessage())));
    }
}