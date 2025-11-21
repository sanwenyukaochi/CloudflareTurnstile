package io.turnstile.cloudflare.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:config/turnstile.properties")
@ConfigurationProperties(prefix = "io.turnstile.cloudflare")
public class TurnstileConfigProperties {
    private String secret;
    private String siteKey;
    private String url;
    private int connectTimeout = 5;
    private int readTimeout = 10;
    private Metrics metrics = new Metrics();

    public String getSecret() {
        return secret;
    }

    public String getSiteKey() {
        return siteKey;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setSiteKey(String siteKey) {
        this.siteKey = siteKey;
    }

    public String getUrl() {
        return url;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public static class Metrics {
        private boolean enabled = true;
        private boolean healthCheckEnabled = true;
        private int errorThreshold = 10;

        public boolean isEnabled() {
            return enabled;
        }

        public boolean isHealthCheckEnabled() {
            return healthCheckEnabled;
        }

        public int getErrorThreshold() {
            return errorThreshold;
        }
    }
}
