package com.tpximpact.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;

public record UrlRequest (
        @NotBlank String fullUrl,
        String customAlias
) {}
