package com.tpximpact.url_shortener.model;

import lombok.Data;

@Data
public class ShortenUrlRequest {
    private String fullUrl;
    private String customAlias;
}
