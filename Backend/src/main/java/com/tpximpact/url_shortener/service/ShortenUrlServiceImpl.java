package com.tpximpact.url_shortener.service;

import com.tpximpact.url_shortener.config.RedirectUrlConfig;
import com.tpximpact.url_shortener.exception.DuplicateAliasException;
import com.tpximpact.url_shortener.model.Alias;
import com.tpximpact.url_shortener.model.dto.UrlDto;
import com.tpximpact.url_shortener.model.ShortenUrlRequest;
import com.tpximpact.url_shortener.repository.UrlShortenerRepository;
import com.tpximpact.url_shortener.util.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class ShortenUrlServiceImpl implements ShortenUrlService {

    @Autowired
    private Environment env;

    @Autowired
    private UrlShortenerRepository urlShortenerRepository;

    @Override
    public UrlDto shortenUrl(ShortenUrlRequest req) {
        if(req.getCustomAlias() == null || req.getCustomAlias().isBlank()){
            int urlLength = env.getProperty("redirect.url.length", Integer.class).intValue();

            String newAlias = RandomStringGenerator.GenerateString(urlLength);
            req.setCustomAlias(newAlias);
        }

        if(urlShortenerRepository.existsById(req.getCustomAlias())){
            throw new DuplicateAliasException(String.format("Alias of '%s' already exists. Please use an alias which is not in use.", req.getCustomAlias()));
        }

        String host = env.getProperty("url.host");
        String port = env.getProperty("url.port");
        String formattedUrl = String.format("%s:%s/%s", host, port, req.getCustomAlias());

        Alias newAlias = Alias.builder()
                .name(req.getCustomAlias())
                .destination(req.getFullUrl())
                .build();

        urlShortenerRepository.save(newAlias);

        UrlDto result = new UrlDto();
        result.setShortUrl(formattedUrl);

        return result;
    }
}
