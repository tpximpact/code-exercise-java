package com.tpximpact.url_shortener;

import com.tpximpact.url_shortener.config.CorsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableCaching
public class UrlShortenerApplication {

	@Autowired
	CorsConfig corsConfig;

	public static void main(String[] args) {
		SpringApplication.run(UrlShortenerApplication.class, args);
	}

	@Bean
	WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {

				registry.addMapping("/**")
						.allowedOrigins(corsConfig.getAllowedOrigins().toArray(new String[]{}))
						.allowedMethods("*")
						.allowedHeaders("*");
			}
		};
	}

	@Bean
	public CacheManager cacheManager() {
		return new ConcurrentMapCacheManager("urls");
	}
}
