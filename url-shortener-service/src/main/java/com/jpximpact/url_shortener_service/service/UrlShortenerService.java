package com.jpximpact.url_shortener_service.service;

import com.jpximpact.url_shortener_service.exception.UrlShortenerExceptions;
import com.jpximpact.url_shortener_service.model.ShortenedUrl;
import com.jpximpact.url_shortener_service.repository.ShortenedUrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class UrlShortenerService {

    private static final String ALIAS_CHARS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ALIAS_LENGTH = 7;
    private static final Pattern VALID_ALIAS = Pattern.compile("^[a-zA-Z0-9_-]{1,50}$");
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ShortenedUrlRepository repository;
    private final String baseUrl;

    public UrlShortenerService(ShortenedUrlRepository repository,
                               @Value("${app.base-url}") String baseUrl) {
        this.repository = repository;
        this.baseUrl = baseUrl;
    }

    public ShortenedUrl shorten(String fullUrl, String customAlias) {
        validateUrl(fullUrl);

        String alias;
        if (customAlias != null && !customAlias.isBlank()) {
            validateCustomAlias(customAlias);
            if (repository.existsById(customAlias)) {
                throw new UrlShortenerExceptions.AliasAlreadyTakenException("Alias '" + customAlias + "' is already taken");
            }
            alias = customAlias;
        } else {
            alias = generateUniqueAlias();
        }

        return repository.save(new ShortenedUrl(alias, fullUrl));
    }

    public ShortenedUrl findByAlias(String alias) {
        return repository.findById(alias)
                .orElseThrow(() -> new UrlShortenerExceptions.AliasNotFoundException("Alias '" + alias + "' not found"));
    }

    public List<ShortenedUrl> listAll() {
        return repository.findAll();
    }

    public void delete(String alias) {
        if (!repository.existsById(alias)) {
            throw new UrlShortenerExceptions.AliasNotFoundException("Alias '" + alias + "' not found");
        }
        repository.deleteById(alias);
    }

    public String buildShortUrl(String alias) {
        return baseUrl + "/" + alias;
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private void validateUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new UrlShortenerExceptions.InvalidUrlException("URL must not be blank");
        }
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
                throw new UrlShortenerExceptions.InvalidUrlException("URL must start with http:// or https://");
            }
            String host = uri.getHost();
            if (host == null || host.isBlank()) {
                throw new UrlShortenerExceptions.InvalidUrlException("URL must have a valid host");
            }
        } catch (URISyntaxException e) {
            throw new UrlShortenerExceptions.InvalidUrlException("Malformed URL: " + e.getMessage());
        }
    }

    private void validateCustomAlias(String alias) {
        if (!VALID_ALIAS.matcher(alias).matches()) {
            throw new UrlShortenerExceptions.InvalidAliasException(
                    "Alias must be 1–50 characters and contain only letters, digits, hyphens, or underscores");
        }
    }

    private String generateUniqueAlias() {
        for (int attempt = 0; attempt < 10; attempt++) {
            StringBuilder sb = new StringBuilder(ALIAS_LENGTH);
            for (int i = 0; i < ALIAS_LENGTH; i++) {
                sb.append(ALIAS_CHARS.charAt(RANDOM.nextInt(ALIAS_CHARS.length())));
            }
            String candidate = sb.toString();
            if (!repository.existsById(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Could not generate a unique alias after 10 attempts");
    }
}