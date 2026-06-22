package com.jpximpact.url_shortener_service.repository;

import com.jpximpact.url_shortener_service.model.ShortenedUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShortenedUrlRepository extends JpaRepository<ShortenedUrl, String> {
}
