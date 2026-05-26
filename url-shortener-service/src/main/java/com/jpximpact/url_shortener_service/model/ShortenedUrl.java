package com.jpximpact.url_shortener_service.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "shortened_urls")
public class ShortenedUrl {
    @Id
    @Column(name = "alias", nullable = false, unique = true)
    private String alias;

    @Column(name = "full_url", nullable = false, length = 2048)
    private String fullUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected ShortenedUrl() {}

    public ShortenedUrl(String alias, String fullUrl) {
        this.alias = alias;
        this.fullUrl = fullUrl;
        this.createdAt = Instant.now();
    }

    public String getAlias()     { return alias; }
    public String getFullUrl()   { return fullUrl; }
    public Instant getCreatedAt(){ return createdAt; }
}
