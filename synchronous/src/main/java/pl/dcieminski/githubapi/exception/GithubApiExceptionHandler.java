package pl.dcieminski.githubapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.Map;

@RestControllerAdvice
public class GithubApiExceptionHandler {

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<Map<String, String>> handleHttpStatusCodeException(HttpStatusCodeException e) {
        return ResponseEntity.status(e.getStatusCode()).body(prepareResponseBody(e.getStatusText()));
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<Map<String, String>> handleRestTemplateException(RestClientException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(prepareResponseBody(e.getMessage()));
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<Map<String, String>> handleResourceAccessException(ResourceAccessException e) {
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(prepareResponseBody(e.getCause().getMessage()));
    }

    private Map<String, String> prepareResponseBody(String message) {
        return Collections.singletonMap("error", message);
    }
}