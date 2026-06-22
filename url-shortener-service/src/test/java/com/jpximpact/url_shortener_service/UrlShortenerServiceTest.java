package com.jpximpact.url_shortener_service;

import com.jpximpact.url_shortener_service.exception.UrlShortenerExceptions;
import com.jpximpact.url_shortener_service.model.ShortenedUrl;
import com.jpximpact.url_shortener_service.repository.ShortenedUrlRepository;
import com.jpximpact.url_shortener_service.service.UrlShortenerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceTest {

    @Mock
    private ShortenedUrlRepository repository;

    private UrlShortenerService service;

    @BeforeEach
    void setup() {
        service = new UrlShortenerService(repository, "http://localhost:8080");
    }

    // ── shorten() ─────────────────────────────────────────────────────────────

    @Test
    void shorten_withValidUrl_generatesRandomAlias() {
        when(repository.existsById(anyString())).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ShortenedUrl result = service.shorten("https://example.com", null);

        assertThat(result.getAlias()).hasSize(7);
        assertThat(result.getFullUrl()).isEqualTo("https://example.com");
        verify(repository).save(any());
    }

    @Test
    void shorten_withCustomAlias_usesThatAlias() {
        when(repository.existsById("my-alias")).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ShortenedUrl result = service.shorten("https://example.com", "my-alias");

        assertThat(result.getAlias()).isEqualTo("my-alias");
    }

    @Test
    void shorten_withTakenCustomAlias_throwsAliasAlreadyTakenException() {
        when(repository.existsById("taken")).thenReturn(true);

        assertThatThrownBy(() -> service.shorten("https://example.com", "taken"))
                .isInstanceOf(UrlShortenerExceptions.AliasAlreadyTakenException.class)
                .hasMessageContaining("taken");
    }

    @Test
    void shorten_withBlankUrl_throwsInvalidUrlException() {
        assertThatThrownBy(() -> service.shorten("", null))
                .isInstanceOf(UrlShortenerExceptions.InvalidUrlException.class);
    }

    @Test
    void shorten_withNonHttpUrl_throwsInvalidUrlException() {
        assertThatThrownBy(() -> service.shorten("ftp://example.com", null))
                .isInstanceOf(UrlShortenerExceptions.InvalidUrlException.class)
                .hasMessageContaining("http");
    }

    @Test
    void shorten_withMalformedUrl_throwsInvalidUrlException() {
        assertThatThrownBy(() -> service.shorten("not a url at all ://", null))
                .isInstanceOf(UrlShortenerExceptions.InvalidUrlException.class);
    }

    @Test
    void shorten_withInvalidAliasCharacters_throwsInvalidAliasException() {
        assertThatThrownBy(() -> service.shorten("https://example.com", "bad alias!"))
                .isInstanceOf(UrlShortenerExceptions.InvalidAliasException.class);
    }

    @Test
    void shorten_withAliasTooLong_throwsInvalidAliasException() {
        String longAlias = "a".repeat(51);
        assertThatThrownBy(() -> service.shorten("https://example.com", longAlias))
                .isInstanceOf(UrlShortenerExceptions.InvalidAliasException.class);
    }

    // ── findByAlias() ─────────────────────────────────────────────────────────

    @Test
    void findByAlias_whenPresent_returnsEntry() {
        ShortenedUrl url = new ShortenedUrl("abc1234", "https://example.com");
        when(repository.findById("abc1234")).thenReturn(Optional.of(url));

        ShortenedUrl result = service.findByAlias("abc1234");

        assertThat(result).isEqualTo(url);
    }

    @Test
    void findByAlias_whenMissing_throwsAliasNotFoundException() {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByAlias("missing"))
                .isInstanceOf(UrlShortenerExceptions.AliasNotFoundException.class)
                .hasMessageContaining("missing");
    }

    // ── delete() ──────────────────────────────────────────────────────────────

    @Test
    void delete_existingAlias_callsRepositoryDelete() {
        when(repository.existsById("abc1234")).thenReturn(true);

        service.delete("abc1234");

        verify(repository).deleteById("abc1234");
    }

    @Test
    void delete_unknownAlias_throwsAliasNotFoundException() {
        when(repository.existsById("gone")).thenReturn(false);

        assertThatThrownBy(() -> service.delete("gone"))
                .isInstanceOf(UrlShortenerExceptions.AliasNotFoundException.class);
    }

    // ── listAll() ─────────────────────────────────────────────────────────────

    @Test
    void listAll_returnsAllSavedUrls() {
        List<ShortenedUrl> urls = List.of(
                new ShortenedUrl("abc", "https://example.com"),
                new ShortenedUrl("xyz", "https://other.com")
        );
        when(repository.findAll()).thenReturn(urls);

        assertThat(service.listAll()).hasSize(2);
    }

    // ── buildShortUrl() ───────────────────────────────────────────────────────

    @Test
    void buildShortUrl_combinesBaseUrlAndAlias() {
        assertThat(service.buildShortUrl("myAlias"))
                .isEqualTo("http://localhost:8080/myAlias");
    }
}