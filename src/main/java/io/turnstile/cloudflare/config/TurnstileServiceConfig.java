package io.turnstile.cloudflare.config;

import io.turnstile.cloudflare.TurnstileConfiguration;
import io.turnstile.cloudflare.service.TurnstileValidationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class TurnstileServiceConfig {
    Logger logger = LoggerFactory.getLogger(TurnstileConfiguration.class);
    private final TurnstileConfigProperties properties;

    public TurnstileServiceConfig(TurnstileConfigProperties properties) {
        this.properties = properties;
    }

    @Bean
    public TurnstileValidationService turnstileValidationService(@Qualifier("turnstileRestClient") RestClient restClient) {
        return new TurnstileValidationService(restClient, properties);
    }

    @Bean(name = "turnstileRestClient")
    public RestClient turnstileRestClient() {
        logger.info("Creating Turnstile REST client with endpoint: {}", properties.getUrl());
        logger.info("Turnstile REST client timeouts - connect: {}s, read: {}s",
                properties.getConnectTimeout(), properties.getReadTimeout());

        // Configure HttpClient with timeouts
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(properties.getConnectTimeout()))
                .build();

        // Create request factory with the configured HttpClient
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(properties.getReadTimeout()));

        return RestClient.builder()
                .baseUrl(properties.getUrl())
                .requestFactory(requestFactory)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

}
