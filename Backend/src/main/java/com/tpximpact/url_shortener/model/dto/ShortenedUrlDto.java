package com.tpximpact.url_shortener.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortenedUrlDto {
    private String alias;
    private String shortUrl;
    private String fullUrl;
}
