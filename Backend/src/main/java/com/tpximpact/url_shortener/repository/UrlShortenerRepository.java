package com.tpximpact.url_shortener.repository;

import com.tpximpact.url_shortener.model.Alias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlShortenerRepository extends JpaRepository<Alias, String> {
}
