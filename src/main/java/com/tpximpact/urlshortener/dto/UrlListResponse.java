package com.tpximpact.urlshortener.dto;

public record UrlListResponse(
        String alias,
        String fullUrl,
        String shortUrl
) {}