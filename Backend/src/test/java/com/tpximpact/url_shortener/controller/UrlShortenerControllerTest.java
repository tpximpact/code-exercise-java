package com.tpximpact.url_shortener.controller;

import com.tpximpact.url_shortener.model.Alias;
import com.tpximpact.url_shortener.model.dto.UrlDto;
import com.tpximpact.url_shortener.model.ShortenUrlRequest;
import com.tpximpact.url_shortener.service.ShortenUrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlShortenerControllerTest {

    @InjectMocks
    UrlShortenerController urlShortenerController;

    @Mock
    ShortenUrlService shortenUrlService;

    @Test
    void urlShortenerShouldReturnBadRequestWhenNoUrlInBody() {
        ShortenUrlRequest req = new ShortenUrlRequest();
        req.setCustomAlias("custom alias");
        ResponseEntity<Object> response = urlShortenerController.shortenUrl(req);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void urlShortenerShouldReturnNewUrlWhenSuccessfulRequest() {
        ShortenUrlRequest req = new ShortenUrlRequest();
        req.setFullUrl("https://newUrl.com");
        UrlDto expectedResult = new UrlDto("new url");
        when(shortenUrlService.shortenUrl(req)).thenReturn(expectedResult);
        ResponseEntity<Object> response = urlShortenerController.shortenUrl(req);
        UrlDto actualResult = (UrlDto) response.getBody();

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(actualResult);
        assertNotNull(actualResult.getShortUrl());
    }

    @Test
    void urlFetchUrlsShouldCallServiceLayer(){
        urlShortenerController.urls();

        verify(shortenUrlService).getAllUrls();
    }

    @Test
    void redirectShouldFetchAliasDetailsFromServiceLayer(){
        String alias = "google";
        String destination = "google.com";
        when(shortenUrlService.findByAlias(alias)).thenReturn(Alias.builder()
                .name(alias)
                .destination(destination)
                .build());
        urlShortenerController.redirect(alias);

        verify(shortenUrlService).findByAlias(alias);
    }

    @Test
    void redirectShouldThrowInternalServerErrorIfItCannotGetFullRedirectLink(){
        String alias = "google";
        when(shortenUrlService.findByAlias(alias)).thenReturn(Alias.builder().build());

        ResponseEntity<Object> redirect = urlShortenerController.redirect(alias);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, redirect.getStatusCode());
    }

    @Test
    void deleteAliasEndpointShouldCallServiceLayer(){
        String alias = "google";
        urlShortenerController.deleteAlias(alias);

        verify(shortenUrlService).deleteAlias(alias);
    }
}