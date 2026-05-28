package com.tpximpact.urlshortener.controller;

import com.tpximpact.urlshortener.dto.UrlListResponse;
import com.tpximpact.urlshortener.dto.UrlRequest;
import com.tpximpact.urlshortener.dto.UrlResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UrlControllerWIT {
    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    @DisplayName("POST /shorten - Should create short URL")
    void shouldCreateShortUrl() {
        // Given
        UrlRequest request = new UrlRequest("https://tpximpact.com", "tpx");

        // When
        ResponseEntity<UrlResponse> response = restTemplate.postForEntity(
                "/shorten",
                request,
                UrlResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().shortUrl()).contains("/tpx");
    }

    @Test
    @DisplayName("GET /{alias} - Should return 302 Redirect")
    void shouldRedirect() {
        // First, ensure the URL exists (Seed the data)
        restTemplate.postForEntity("/shorten", new UrlRequest("https://google.com", "goog"), UrlResponse.class);

        // When - We use getForEntity
        ResponseEntity<Void> response = restTemplate.getForEntity("/{alias}", Void.class, "goog");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("GET /urls - Should return list using TestRestTemplate")
    void getAllRestTemplate() {
        ResponseEntity<UrlListResponse[]> response = restTemplate.getForEntity(
                "/urls",
                UrlListResponse[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Convert array to list for easier assertions
        List<UrlListResponse> urls = Arrays.asList(response.getBody());
        assertThat(urls).isNotEmpty();
        assertThat(urls.get(0).alias()).isEqualTo("goog");


    }

    @Test
    @DisplayName("DELETE /{alias} - Should remove entry")
    void shouldDelete() {
        // When
        restTemplate.delete("/{alias}", "tpx");

        // Then - Verify 404 after deletion
        ResponseEntity<Void> response = restTemplate.getForEntity("/{alias}", Void.class, "tpx");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}
