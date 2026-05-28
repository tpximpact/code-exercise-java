package com.tpximpact.url_shortener.controller;

import com.tpximpact.url_shortener.model.ShortenUrlRequest;
import com.tpximpact.url_shortener.common.dto.UrlDto;
import com.tpximpact.url_shortener.service.ShortenUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UrlShortenerController {

    @Autowired
    private ShortenUrlService shortenUrlService;

    @PostMapping("/shorten")
    public ResponseEntity<Object> shortenUrl(@RequestBody ShortenUrlRequest url){
        if(url.getFullUrl() == null || url.getFullUrl().isBlank()){
            return ResponseEntity.badRequest().body("Invalid input");
        }

        UrlDto urlDto = shortenUrlService.shortenUrl(url);

        return ResponseEntity.ok(urlDto);
    }
}
