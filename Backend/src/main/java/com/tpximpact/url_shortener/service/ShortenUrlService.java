package com.tpximpact.url_shortener.service;

import com.tpximpact.url_shortener.common.dto.UrlDto;
import com.tpximpact.url_shortener.model.ShortenUrlRequest;

public interface ShortenUrlService {
    UrlDto shortenUrl(ShortenUrlRequest req);
}
