package com.tpximpact.url_shortener.service;

import com.tpximpact.url_shortener.model.Alias;
import com.tpximpact.url_shortener.model.dto.ShortenedUrlDto;
import com.tpximpact.url_shortener.model.dto.UrlDto;
import com.tpximpact.url_shortener.model.ShortenUrlRequest;

import java.util.List;

public interface ShortenUrlService {
    UrlDto shortenUrl(ShortenUrlRequest req);

    List<ShortenedUrlDto> getAllUrls();

    Alias findByAlias(String aliasName);

    void deleteAlias(String alias);
}
