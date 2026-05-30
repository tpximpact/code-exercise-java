package com.tpximpact.url_shortener.service;

import com.tpximpact.url_shortener.exception.DuplicateAliasException;
import com.tpximpact.url_shortener.model.Alias;
import com.tpximpact.url_shortener.model.dto.UrlDto;
import com.tpximpact.url_shortener.model.ShortenUrlRequest;
import com.tpximpact.url_shortener.repository.UrlShortenerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShortenUrlServiceImplTest {

    @Mock
    Environment env;

    @Mock
    UrlShortenerRepository urlShortenerRepository;

    @InjectMocks
    private ShortenUrlServiceImpl shortenUrlService;

    private static final String HOST = "test-host";

    private static final String PORT = "8080";

    @Test
    void shortenUrlServiceShouldReturnRandomizedUrl() {
        when(env.getProperty("redirect.url.length", Integer.class)).thenReturn(6);
        String url = "https://google.com";
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setFullUrl(url);

        UrlDto result = shortenUrlService.shortenUrl(request);

        assertNotEquals(url, result.getShortUrl());
    }

    @Test
    void shortenUrlServiceShouldReturnAliasIfProvided() {
        when(env.getProperty("url.host")).thenReturn(HOST);
        when(env.getProperty("url.port")).thenReturn(PORT);
        String url = "https://google.com";
        String alias = "testalias";
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setFullUrl(url);
        request.setCustomAlias(alias);

        UrlDto result = shortenUrlService.shortenUrl(request);

        assertEquals(String.format("%s:%s/%s", HOST, PORT, alias), result.getShortUrl());
    }

    @Test
    void shortenUrlServiceShouldThrowExceptionIfUrlAliasIsAlreadyInUse(){
        when(env.getProperty("url.host")).thenReturn(HOST);
        when(env.getProperty("url.port")).thenReturn(PORT);
        String url = "https://google.com";
        String alias = "testalias";
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setFullUrl(url);
        request.setCustomAlias(alias);
        when(urlShortenerRepository.existsById(alias)).thenReturn(true);

        assertThrows(DuplicateAliasException.class, ()->shortenUrlService.shortenUrl(request));
    }

    @Test
    void shortenUrlServiceShouldSaveAliasToPersistenceLayerWhenValid(){
        when(env.getProperty("url.host")).thenReturn(HOST);
        when(env.getProperty("url.port")).thenReturn(PORT);
        String url = "https://google.com";
        String alias = "testalias";
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setFullUrl(url);
        request.setCustomAlias(alias);

        shortenUrlService.shortenUrl(request);

        verify(urlShortenerRepository).save(any(Alias.class));
    }
}