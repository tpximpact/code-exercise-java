package com.tpximpact.urlshortener.service;

import com.tpximpact.urlshortener.dto.UrlRequest;
import com.tpximpact.urlshortener.entity.Url;
import com.tpximpact.urlshortener.exception.BadRequestException;
import com.tpximpact.urlshortener.exception.NotFoundException;
import com.tpximpact.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UrlServiceImplTest {

    @Mock
    private UrlRepository repository;

    @InjectMocks
    private UrlServiceImpl service;

    private UrlRequest request;
    private Url url;

    @BeforeEach
    public void setUp() {
        request = new UrlRequest("https://google.com", "alias1");

        url = Url.builder()
                .alias("alias1")
                .fullUrl("https://google.com")
                .build();
    }

    @Test
    void shouldCreateShortUrl_withCustomAlias() {

        when(repository.existsByAlias("alias1")).thenReturn(false);

        var response = service.shorten(request);

        assertThat(response.shortUrl()).contains("alias1");
        verify(repository).existsByAlias("alias1");
    }

    @Test
    void shouldThrowException_whenAliasAlreadyExists() {

        when(repository.existsByAlias("alias1")).thenReturn(true);

        assertThatThrownBy(() -> service.shorten(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid input or alias already taken");
    }

    @Test
    void shouldReturnFullUrl_whenAliasExists() {

        when(repository.findByAlias("alias1")).thenReturn(Optional.of(url));

        var result = service.getFullUrl("alias1");

        assertThat(result).isEqualTo("https://google.com");
    }

    @Test
    void shouldThrowNotFound_whenAliasMissing() {
        when(repository.findByAlias("abc")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getFullUrl("abc"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldDelete_whenAliasExists() {
        Url url = Url.builder().alias("abc").build();

        when(repository.findByAlias("abc")).thenReturn(Optional.of(url));

        service.delete("abc");

        verify(repository).delete(url);
    }
}