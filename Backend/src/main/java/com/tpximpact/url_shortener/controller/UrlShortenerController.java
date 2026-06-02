package com.tpximpact.url_shortener.controller;

import com.tpximpact.url_shortener.model.Alias;
import com.tpximpact.url_shortener.model.ShortenUrlRequest;
import com.tpximpact.url_shortener.model.dto.ShortenedUrlDto;
import com.tpximpact.url_shortener.model.dto.UrlDto;
import com.tpximpact.url_shortener.service.ShortenUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UrlShortenerController {

    @Autowired
    private ShortenUrlService shortenUrlService;

    @PostMapping("/shorten")
    public ResponseEntity<Object> shortenUrl(@RequestBody ShortenUrlRequest url){
        System.out.println("shorten url request received");
        if(url.getFullUrl() == null || url.getFullUrl().isBlank()){
            System.out.println("shorten url request had an empty url");
            return ResponseEntity.badRequest().body("Invalid input");
        }

        UrlDto urlDto = shortenUrlService.shortenUrl(url);

        System.out.println("shorten url request sucessfully returned");

        return ResponseEntity.ok(urlDto);
    }

    @GetMapping("/urls")
    public ResponseEntity<Object> urls(){
        System.out.println("url list request received");
        List<ShortenedUrlDto> allUrls = shortenUrlService.getAllUrls();
        System.out.println("url list request successfully returned to sender");

        return ResponseEntity.ok(allUrls);
    }

    @GetMapping("/{alias}")
    public ResponseEntity<Object> redirect(@PathVariable String alias){
        System.out.println("redirect request recieved");
        Alias result = shortenUrlService.findByAlias(alias);
        String fullUrlDestination = result.getDestination();
        if(fullUrlDestination == null || fullUrlDestination.isBlank()){
            return ResponseEntity.internalServerError().body("Full url destination missing, cannot redirect using this alias");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", fullUrlDestination);

        System.out.println("redirect request success");
        return new ResponseEntity(headers, HttpStatus.resolve(302));
    }

    @DeleteMapping("/{alias}")
    public ResponseEntity<Object> deleteAlias(@PathVariable String alias){
        shortenUrlService.deleteAlias(alias);

        return ResponseEntity.status(204).body("successfully deleted");
    }
}
