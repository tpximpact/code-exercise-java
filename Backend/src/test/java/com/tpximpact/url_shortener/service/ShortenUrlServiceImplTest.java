package com.tpximpact.url_shortener.service;

import com.tpximpact.url_shortener.config.RedirectUrlConfig;
import com.tpximpact.url_shortener.exception.AliasDoesNotExistException;
import com.tpximpact.url_shortener.exception.DuplicateAliasException;
import com.tpximpact.url_shortener.model.Alias;
import com.tpximpact.url_shortener.model.dto.ShortenedUrlDto;
import com.tpximpact.url_shortener.model.dto.UrlDto;
import com.tpximpact.url_shortener.model.ShortenUrlRequest;
import com.tpximpact.url_shortener.repository.UrlShortenerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShortenUrlServiceImplTest {

    @Mock
    RedirectUrlConfig urlConfig;

    @Mock
    UrlShortenerRepository urlShortenerRepository;

    @InjectMocks
    private ShortenUrlServiceImpl shortenUrlService;

    private static final String PROTOCOL = "http";

    private static final String HOST = "test-host";

    private static final String PORT = "8080";

    @Test
    void shortenUrlServiceShouldReturnRandomizedUrl() {
        when(urlConfig.getLength()).thenReturn(6);
        String url = "https://google.com";
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setFullUrl(url);

        UrlDto result = shortenUrlService.shortenUrl(request);

        assertNotEquals(url, result.getShortUrl());
    }

    @Test
    void shortenUrlServiceShouldReturnAliasIfProvided() {
        when(urlConfig.getProtocol()).thenReturn(PROTOCOL);
        when(urlConfig.getHost()).thenReturn(HOST);
        when(urlConfig.getPort()).thenReturn(PORT);
        String url = "https://google.com";
        String alias = "testalias";
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setFullUrl(url);
        request.setCustomAlias(alias);

        UrlDto result = shortenUrlService.shortenUrl(request);

        assertEquals(String.format("%s://%s:%s/%s", PROTOCOL, HOST, PORT, alias), result.getShortUrl());
    }

    @Test
    void shortenUrlServiceShouldThrowExceptionIfUrlAliasIsAlreadyInUse(){
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
        when(urlConfig.getHost()).thenReturn(HOST);
        when(urlConfig.getPort()).thenReturn(PORT);
        String url = "https://google.com";
        String alias = "testalias";
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setFullUrl(url);
        request.setCustomAlias(alias);

        shortenUrlService.shortenUrl(request);

        verify(urlShortenerRepository).save(any(Alias.class));
    }

    @Test
    void getAllUrlsShouldReturnAListOfDtosWhichAreFormattedFromPersistenceLayer(){
        when(urlConfig.getProtocol()).thenReturn(PROTOCOL);
        when(urlConfig.getHost()).thenReturn(HOST);
        when(urlConfig.getPort()).thenReturn(PORT);
        String destination1 = "https://google.com";
        String name1 = "google";
        Alias alias1 = Alias.builder().name(name1).destination(destination1).build();
        String destination2 = "https://amazon.com";
        String name2 = "amazon";
        Alias alias2 = Alias.builder().name(name2).destination(destination2).build();
        List<Alias> aliasList = List.of(alias1, alias2);

        when(urlShortenerRepository.findAll()).thenReturn(aliasList);

        List<ShortenedUrlDto> resultList = shortenUrlService.getAllUrls();

        for(ShortenedUrlDto result : resultList){
            assertTrue(result.getAlias().equals(name1) || result.getAlias().equals(name2));
            assertTrue(result.getFullUrl().equals(destination1) || result.getFullUrl().equals(destination2));
            String shortUrl1 = String.format(String.format("%s://%s:%s/%s", PROTOCOL, HOST, PORT, alias1.getName()));
            String shortUrl2 = String.format(String.format("%s://%s:%s/%s", PROTOCOL, HOST, PORT, alias2.getName()));
            assertTrue(result.getShortUrl().equals(shortUrl1) || result.getShortUrl().equals(shortUrl2));
        }
    }

    @Test
    void findUrlByAliasShouldASingleShortenedUrl(){
        String name = "google";
        String destination = "https://google.com";
        Alias expectedResult = Alias.builder()
                .name(name)
                .destination(destination)
                .build();
        when(urlShortenerRepository.findById(name)).thenReturn(Optional.ofNullable(expectedResult));

        Alias actualResult = shortenUrlService.findByAlias(name);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void findUrlByAliasShouldThrowExceptionWhenAliasDoesNotExist(){
        String aliasName = "google";
        when(urlShortenerRepository.findById(aliasName)).thenReturn(Optional.empty());

        assertThrows(AliasDoesNotExistException.class, ()->shortenUrlService.findByAlias(aliasName));
    }

    @Test
    void deleteAliasShouldCallPersistenceLayer(){
        String aliasName = "google";
        Alias alias = Alias.builder().name(aliasName).build();
        when(urlShortenerRepository.findById(aliasName)).thenReturn(Optional.ofNullable(alias));

        shortenUrlService.deleteAlias(aliasName);
        verify(urlShortenerRepository).deleteById(aliasName);
    }

    @Test
    void deleteAliasShouldThrowNotFoundExceptionIfAliasDoesNotExist(){
        String aliasName = "google";
        when(urlShortenerRepository.findById(aliasName)).thenReturn(Optional.empty());

        assertThrows(AliasDoesNotExistException.class, ()->shortenUrlService.deleteAlias(aliasName));
    }
}