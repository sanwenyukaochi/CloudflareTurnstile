package io.github.sanwenyukaochi.cloudflare.turnstile.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Cloudflare Turnstile 集成的配置属性类。
 *
 * @see <a href="https://developers.cloudflare.com/turnstile/">Cloudflare Turnstile 官方文档</a>
 */
@Data
@Component
@PropertySource("classpath:config/turnstile.properties")
@ConfigurationProperties(prefix = "io.github.sanwenyukaochi.cloudflare.turnstile")
public class TurnstileConfigProperties {
    private String secret;
    private String siteKey;
    private String url;
    private int connectTimeout = 5;
    private int readTimeout = 10;
    private Metrics metrics = new Metrics();

    @Data
    public static class Metrics {
        private boolean enabled = true;
        private boolean healthCheckEnabled = true;
        private int errorThreshold = 10;
    }
}
