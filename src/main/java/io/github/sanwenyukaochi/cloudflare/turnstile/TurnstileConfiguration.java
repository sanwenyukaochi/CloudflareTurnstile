package io.github.sanwenyukaochi.cloudflare.turnstile;

import io.github.sanwenyukaochi.cloudflare.turnstile.config.TurnstileConfigProperties;
import io.github.sanwenyukaochi.cloudflare.turnstile.config.TurnstileHealthIndicator;
import io.github.sanwenyukaochi.cloudflare.turnstile.config.TurnstileMetricsConfig;
import io.github.sanwenyukaochi.cloudflare.turnstile.config.TurnstileServiceConfig;
import io.github.sanwenyukaochi.cloudflare.turnstile.filter.TurnstileCaptchaFilter;
import io.github.sanwenyukaochi.cloudflare.turnstile.service.TurnstileValidationService;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.health.autoconfigure.contributor.ConditionalOnEnabledHealthIndicator;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Main auto-configuration class for the Spring Cloudflare Turnstile integration.
 * <p>
 * This class serves as the entry point for Spring Boot's auto-configuration mechanism to automatically set up Cloudflare Turnstile integration when
 * the library is included in a project. It imports the necessary configuration components such as property management, service configuration, and
 * metrics/monitoring configuration.
 * </p>
 * <p>
 * To use this auto-configuration, include this library in your Spring Boot project and configure the required properties in your application.yml or
 * application.properties file:
 * </p>
 *
 * <pre>
 * ds:
 *   cf:
 *     turnstile:
 *       sitekey: your-turnstile-site-key
 *       secret: your-turnstile-secret-key
 *       url: <a href="https://challenges.cloudflare.com/turnstile/v0/siteverify">...</a>
 *       metrics:
 *         enabled: true
 *         health-check-enabled: true
 *         error-threshold: 10
 * </pre>
 * <p>
 * The {@link #onStartup()} method is annotated with {@link PostConstruct} and is executed after the bean initialization to log a
 * confirmation message that the Cloudflare Turnstile Service has been loaded.
 * </p>
 *
 * @see TurnstileConfigProperties
 * @see TurnstileServiceConfig
 * @see TurnstileValidationService
 * @see TurnstileMetricsConfig
 * @see TurnstileHealthIndicator
 */
@Slf4j
@Configuration
@AutoConfiguration
@Import({TurnstileServiceConfig.class, TurnstileConfigProperties.class, TurnstileCaptchaFilter.class})
public class TurnstileConfiguration {

    /**
     * Metrics configuration for Turnstile. Only imported if MeterRegistry is available on the classpath.
     */
    @Configuration
    @ConditionalOnClass(MeterRegistry.class)
    @Import(TurnstileMetricsConfig.class)
    static class TurnstileMetricsConfiguration {}

    /**
     * Health indicator configuration for Turnstile. Only imported if Spring Actuator health is enabled.
     */
    @Configuration
    @ConditionalOnEnabledHealthIndicator("turnstile")
    @ConditionalOnClass(name = "org.springframework.boot.health.contributor.HealthIndicator")
    @Import(TurnstileHealthIndicator.class)
    static class TurnstileHealthConfiguration {}

    /**
     * Method executed after the bean initialization.
     * <p>
     * This method logs a message indicating that the Cloudflare Turnstile Service has been loaded.
     * </p>
     */
    @PostConstruct
    public void onStartup() {
        log.info("Spring Cloudflare Turnstile Service loaded");
    }
}
