package com.tpximpact.url_shortener.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

//@ConfigurationProperties(prefix = "redirect.url")
@Configuration
public class RedirectUrlConfig {

    @Value("${redirect.url.length}")
    private int length;

    @Value("${url.host}")
    private String host;

    @Value("${url.port}")
    private String port;
}
