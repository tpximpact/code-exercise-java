package com.tpximpact.url_shortener.service;

import com.tpximpact.url_shortener.config.RedirectUrlConfig;
import com.tpximpact.url_shortener.exception.AliasDoesNotExistException;
import com.tpximpact.url_shortener.exception.DuplicateAliasException;
import com.tpximpact.url_shortener.exception.UnacceptableUrlException;
import com.tpximpact.url_shortener.model.Alias;
import com.tpximpact.url_shortener.model.dto.ShortenedUrlDto;
import com.tpximpact.url_shortener.model.dto.UrlDto;
import com.tpximpact.url_shortener.model.ShortenUrlRequest;
import com.tpximpact.url_shortener.repository.UrlShortenerRepository;
import com.tpximpact.url_shortener.util.RandomStringGenerator;
import com.tpximpact.url_shortener.util.ValidatorHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ShortenUrlServiceImpl implements ShortenUrlService {

    @Autowired
    private RedirectUrlConfig urlConfig;

    @Autowired
    private UrlShortenerRepository urlShortenerRepository;

    @Override
    public UrlDto shortenUrl(ShortenUrlRequest req) {
        if(req.getCustomAlias() == null || req.getCustomAlias().isBlank()){
            int urlLength = urlConfig.getLength();

            String newAlias = RandomStringGenerator.GenerateString(urlLength);
            req.setCustomAlias(newAlias);
        }

        if(urlShortenerRepository.existsById(req.getCustomAlias())){
            throw new DuplicateAliasException(String.format("Invalid input or alias already taken: %s", req.getCustomAlias()));
        }

        if(!ValidatorHelper.isValidUrl(req.getFullUrl())){
            throw new UnacceptableUrlException("Unacceptable Url");
        }

        String protocol = ValidatorHelper.getUrlProtocol(req.getFullUrl());
        String host = urlConfig.getHost();
        String port = urlConfig.getPort();
        String formattedUrl = String.format("%s://%s:%s/%s", protocol, host, port, req.getCustomAlias());

        Alias newAlias = Alias.builder()
                .name(req.getCustomAlias())
                .destination(req.getFullUrl())
                .build();

        urlShortenerRepository.save(newAlias);

        UrlDto result = new UrlDto();
        result.setShortUrl(formattedUrl);

        return result;
    }

    @Override
    public List<ShortenedUrlDto> getAllUrls() {
        List<Alias> aliasList = urlShortenerRepository.findAll();

        String host = urlConfig.getHost();
        String port = urlConfig.getPort();
        List<ShortenedUrlDto> resultList = new ArrayList<>();
        for(Alias alias : aliasList){
            ShortenedUrlDto dto = ShortenedUrlDto.builder()
                    .alias(alias.getName())
                    .fullUrl(alias.getDestination())
                    .shortUrl(String.format("%s:%s/%s", host, port, alias.getName()))
                    .build();

            resultList.add(dto);
        }

        return resultList;
    }

    @Override
    public Alias findByAlias(String aliasName) {
        Optional<Alias> aliasResult = urlShortenerRepository.findById(aliasName);

        if(aliasResult.isEmpty()){
            throw new AliasDoesNotExistException(String.format("Alias not found: %s", aliasName));
        }

        return aliasResult.get();
    }

    @Override
    public void deleteAlias(String alias) {
        boolean isEmpty = urlShortenerRepository.findById(alias).isEmpty();

        if(isEmpty){
            throw new AliasDoesNotExistException("Alias not found");
        }
        urlShortenerRepository.deleteById(alias);
    }
}
