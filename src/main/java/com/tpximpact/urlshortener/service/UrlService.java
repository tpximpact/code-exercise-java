package com.tpximpact.urlshortener.service;

import com.tpximpact.urlshortener.dto.UrlListResponse;
import com.tpximpact.urlshortener.dto.UrlRequest;
import com.tpximpact.urlshortener.dto.UrlResponse;

import java.util.List;

public interface UrlService {

    UrlResponse shorten(UrlRequest request);

    String getFullUrl(String alias);

    List<UrlListResponse> getAll();

    void delete(String alias);
}
