package com.tpximpact.url_shortener.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@PropertySource("classpath:application.properties")
@Configuration
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CorsConfig {
    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;
}
