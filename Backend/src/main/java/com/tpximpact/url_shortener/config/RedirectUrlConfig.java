package com.tpximpact.url_shortener.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:application.properties")
@Configuration
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RedirectUrlConfig {

    @Value("${redirect.url.length}")
    private int length;

    @Value("${redirect.url.protocol:http}")
    private String protocol;

    @Value("${redirect.url.host}")
    private String host;

    @Value("${redirect.url.port}")
    private String port;
}
