package com.jpximpact.url_shortener_service.controller;

import com.jpximpact.url_shortener_service.model.ShortenedUrl;
import com.jpximpact.url_shortener_service.service.UrlShortenerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class UrlShortenerController {

    private final UrlShortenerService service;

    public UrlShortenerController(UrlShortenerService service) {
        this.service = service;
    }

    // POST /shorten
    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponse> shorten(@Valid @RequestBody ShortenRequest request) {
        ShortenedUrl saved = service.shorten(request.fullUrl(), request.customAlias());
        String shortUrl = service.buildShortUrl(saved.getAlias());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ShortenResponse(shortUrl));
    }

    // GET /{alias} — redirect
    @GetMapping("/{alias}")
    public ResponseEntity<Void> redirect(@PathVariable String alias) {
        ShortenedUrl url = service.findByAlias(alias);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url.getFullUrl()));
        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
    }

    // DELETE /{alias}
    @DeleteMapping("/{alias}")
    public ResponseEntity<Void> delete(@PathVariable String alias) {
        service.delete(alias);
        return ResponseEntity.noContent().build();
    }

    // GET /urls
    @GetMapping("/urls")
    public ResponseEntity<List<UrlEntry>> listAll() {
        List<UrlEntry> entries = service.listAll().stream()
                .map(u -> new UrlEntry(u.getAlias(), u.getFullUrl(), service.buildShortUrl(u.getAlias())))
                .toList();
        return ResponseEntity.ok(entries);
    }

    // ── Request / Response records ─────────────────────────────────────────────

    public record ShortenRequest(
            @NotBlank(message = "fullUrl is required") String fullUrl,
            String customAlias
    ) {}

    public record ShortenResponse(String shortUrl) {}

    public record UrlEntry(String alias, String fullUrl, String shortUrl) {}
}