package com.tpximpact.urlshortener.repository;

import com.tpximpact.urlshortener.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    boolean existsByAlias(String alias);

    Optional<Url> findByAlias(String alias);
}
