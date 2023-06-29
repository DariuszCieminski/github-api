package pl.dcieminski.githubapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@RestControllerAdvice
public class GithubApiExceptionHandler {

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Map<String, String>> handleWebClientResponseException(WebClientResponseException e) {
        return ResponseEntity.status(e.getStatusCode()).body(prepareResponseBody(e.getStatusText()));
    }

    @ExceptionHandler(WebClientException.class)
    public ResponseEntity<Map<String, String>> handleWebClientException(WebClientException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(prepareResponseBody(e.getMessage()));
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<Map<String, String>> handleTimeoutException() {
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                             .body(prepareResponseBody(HttpStatus.REQUEST_TIMEOUT.getReasonPhrase()));
    }

    private Map<String, String> prepareResponseBody(String message) {
        return Collections.singletonMap("error", message);
    }
}