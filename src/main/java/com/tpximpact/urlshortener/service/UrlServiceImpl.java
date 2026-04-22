package com.tpximpact.urlshortener.service;

import com.tpximpact.urlshortener.dto.UrlListResponse;
import com.tpximpact.urlshortener.dto.UrlRequest;
import com.tpximpact.urlshortener.dto.UrlResponse;
import com.tpximpact.urlshortener.entity.Url;
import com.tpximpact.urlshortener.exception.BadRequestException;
import com.tpximpact.urlshortener.exception.NotFoundException;
import com.tpximpact.urlshortener.repository.UrlRepository;
import com.tpximpact.urlshortener.util.UrlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private static final String BASE_URL = "http://localhost:8080/";
    private final UrlRepository urlRepository;

    @Override
    public UrlResponse shorten(UrlRequest request) {

        String alias = request.customAlias() != null
                ? request.customAlias() : UrlUtils.generateAlias();

        if (urlRepository.existsByAlias(alias)) {
            throw new BadRequestException("Invalid input or alias already taken");
        }

        Url url = Url
                .builder()
                .fullUrl(request.fullUrl())
                .alias(alias)
                .build();

        urlRepository.save(url);

        return new UrlResponse(BASE_URL + alias);
    }

    @Override
    public String getFullUrl(String alias) {
        log.info("Getting full url for alias ' {} ' from the DB", alias);
        return urlRepository.findByAlias(alias)
                .map(Url::getFullUrl)
                .orElseThrow(() -> new NotFoundException("Alias not found"));
    }

    @Override
    public List<UrlListResponse> getAll() {
        return urlRepository.findAll().stream()
                .map(url -> new UrlListResponse(
                        url.getAlias(),
                        url.getFullUrl(),
                        BASE_URL + url.getAlias()
                ))
                .toList();
    }

    public void delete(String alias) {

        Url url = urlRepository.findByAlias(alias)
                .orElseThrow(() -> new NotFoundException("Alias not found"));
        log.info("Deleting shorten url for alias '{}' from the DB", url.getAlias());
        urlRepository.delete(url);
    }
}
