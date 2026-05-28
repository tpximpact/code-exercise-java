package com.tpximpact.urlshortener.controller;

import com.tpximpact.urlshortener.dto.UrlListResponse;
import com.tpximpact.urlshortener.dto.UrlRequest;
import com.tpximpact.urlshortener.dto.UrlResponse;
import com.tpximpact.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequiredArgsConstructor
@Slf4j
class UrlController {
    private final UrlService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<UrlResponse> shorten(@RequestBody @Valid UrlRequest request) {
        UrlResponse response = urlService.shorten(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{alias}")
    public ResponseEntity<Void> redirect(@PathVariable String alias) {
        log.info("Contacting urlService to full url with: {}", alias);
        String fullUrl = urlService.getFullUrl(alias);
        log.info("Received Full url to redirect: {}", fullUrl);
        return ResponseEntity.status(302)
                .location(URI.create(fullUrl))
                .build();
    }

    @GetMapping("/urls")
    public ResponseEntity<List<UrlListResponse>> getAll() {
        return ResponseEntity.ok(urlService.getAll());
    }

    @DeleteMapping("/{alias}")
    public ResponseEntity<Void> delete(@PathVariable String alias) {
        urlService.delete(alias);
        return ResponseEntity.noContent().build();
    }

}
