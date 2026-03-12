package io.github.sanwenyukaochi.cloudflare.turnstile.config;

import io.github.sanwenyukaochi.cloudflare.turnstile.service.TurnstileValidationService;
import io.micrometer.core.instrument.MeterRegistry;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * 用于设置 Turnstile 相关 bean 的配置类。此类配置用于 Turnstile API 交互的 RestTemplate 和 RestClient。
 * 并初始化 TurnstileValidationService。
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class TurnstileServiceConfig {

    private final TurnstileConfigProperties properties;
    private final ObjectProvider<MeterRegistry> meterRegistryProvider;

    /**
     * 创建一个 TurnstileValidationService bean。
     *
     * @param restClient 用于 Turnstile 调用的预配置 REST 客户端
     * @return 一个已配置的 TurnstileValidationService 实例
     */
    @Bean
    public TurnstileValidationService turnstileValidationService(
            @Qualifier("turnstileRestClient") RestClient restClient) {
        Optional<MeterRegistry> optionalRegistry = Optional.ofNullable(meterRegistryProvider.getIfAvailable());
        return new TurnstileValidationService(restClient, properties, optionalRegistry);
    }

    /**
     * 创建一个用于 Turnstile API 交互的 RestClient bean。
     *
     * @return 一个已配置的 RestClient 实例
     */
    @Bean(name = "turnstileRestClient")
    public RestClient turnstileRestClient() {
        log.info("Creating Turnstile REST client with endpoint: {}", properties.getUrl());
        log.info(
                "Turnstile REST client timeouts - connect: {}s, read: {}s",
                properties.getConnectTimeout(),
                properties.getReadTimeout());

        // 配置 HttpClient 的超时时间
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(properties.getConnectTimeout()))
                .build();

        // 使用已配置的 HttpClient 创建请求工厂
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
