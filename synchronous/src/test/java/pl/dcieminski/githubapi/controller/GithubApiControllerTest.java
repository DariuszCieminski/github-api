package pl.dcieminski.githubapi.controller;

import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import pl.dcieminski.githubapi.mapper.GithubUserMapperImpl;
import pl.dcieminski.githubapi.model.GithubUser;
import pl.dcieminski.githubapi.service.GithubService;
import pl.dcieminski.githubapi.util.TestData;

import java.net.SocketTimeoutException;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GithubApiController.class)
@Import(GithubUserMapperImpl.class)
@MockitoSettings
class GithubApiControllerTest {

    @MockBean
    private GithubService githubService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_ReturnOkWithCorrectResponseBody_when_ValidLoginIsProvided() throws Exception {
        GithubUser githubUser = TestData.getGithubUser();
        when(githubService.getGithubUserByLogin(githubUser.getLogin())).thenReturn(githubUser);

        mockMvc.perform(get("/users/{login}", githubUser.getLogin()))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.id", is(notNullValue())))
               .andExpect(jsonPath("$.login", is(equalTo(githubUser.getLogin()))))
               .andExpect(jsonPath("$.name", is(equalTo(githubUser.getName()))))
               .andExpect(jsonPath("$.type", is(equalTo(githubUser.getType()))))
               .andExpect(jsonPath("$.avatarUrl", is(equalTo(githubUser.getAvatarUrl()))))
               .andExpect(jsonPath("$.createdAt", is(equalTo(githubUser.getCreatedAt().toString()))))
               .andExpect(jsonPath("$.calculations", is(closeTo(8.000, 0.000))));
    }

    @Test
    void should_Return404_when_LoginDoesNotExist() throws Exception {
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.NOT_FOUND);
        when(githubService.getGithubUserByLogin(anyString())).thenThrow(exception);

        mockMvc.perform(get("/users/{login}", "nonExistentLogin"))
               .andExpect(status().isNotFound())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.error", is(equalTo(exception.getStatusText()))));
    }

    @Test
    void should_Return408_when_ConnectionTimeoutOccurs() throws Exception {
        ResourceAccessException exception = new ResourceAccessException("Connection Error", new SocketTimeoutException("Timeout"));
        when(githubService.getGithubUserByLogin(anyString())).thenThrow(exception);

        mockMvc.perform(get("/users/{login}", "validLogin"))
               .andExpect(status().isRequestTimeout())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.error", is(equalTo(exception.getCause().getMessage()))));
    }
}