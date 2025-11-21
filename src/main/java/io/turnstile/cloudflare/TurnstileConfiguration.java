package io.turnstile.cloudflare;

import io.turnstile.cloudflare.config.TurnstileConfigProperties;
import io.turnstile.cloudflare.config.TurnstileServiceConfig;
import io.turnstile.cloudflare.filter.TurnstileCaptchaFilter;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@AutoConfiguration
@Import({TurnstileServiceConfig.class, TurnstileConfigProperties.class, TurnstileCaptchaFilter.class})
public class TurnstileConfiguration {

    Logger logger = LoggerFactory.getLogger(TurnstileConfiguration.class);
    
    @PostConstruct
    public void onStartup() {
        logger.info("DigitalSanctuary Spring Cloudflare Turnstile Service loaded");
    }
}
