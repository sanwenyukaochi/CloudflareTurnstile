package io.github.sanwenyukaochi.cloudflare.turnstile.config;

import io.github.sanwenyukaochi.cloudflare.turnstile.service.TurnstileValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Cloudflare Turnstile 服务运行状况指示器。
 * <p>
 * 此组件提供 Cloudflare Turnstile 服务运行状况检查信息。它会检查服务是否配置正确，以及
 * 服务是否已超出配置的错误阈值。可以通过配置禁用此运行状况指示器。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "io.github.sanwenyukaochi.cloudflare.turnstile.metrics",
        name = "health-check-enabled",
        havingValue = "true",
        matchIfMissing = true)
public class TurnstileHealthIndicator implements HealthIndicator {

    private final TurnstileValidationService validationService;
    private final TurnstileConfigProperties properties;

    @Override
    public Health health() {
        try {
            // 检查服务是否配置正确。
            if (properties.getSecret() == null || properties.getSecret().isBlank()) {
                return Health.down()
                        .withDetail("reason", "Turnstile secret key is not configured")
                        .build();
            }

            if (properties.getUrl() == null || properties.getUrl().isBlank()) {
                return Health.down()
                        .withDetail("reason", "Turnstile URL is not configured")
                        .build();
            }

            // 检查错误率是否低于阈值
            double errorRate = validationService.getErrorRate();
            int errorThreshold = properties.getMetrics().getErrorThreshold();

            Health.Builder builder = Health.up()
                    .withDetail("url", properties.getUrl())
                    .withDetail("validationCount", validationService.getValidationCount())
                    .withDetail("successCount", validationService.getSuccessCount())
                    .withDetail("errorCount", validationService.getErrorCount())
                    .withDetail("errorRate", String.format("%.2f%%", errorRate))
                    .withDetail("responseTimeAvg", String.format("%.2fms", validationService.getAverageResponseTime()));

            // 如果错误率超过阈值，则报告为“故障”
            if (errorRate > errorThreshold) {
                return builder.down()
                        .withDetail(
                                "reason", "Error rate exceeded threshold: " + errorRate + "% > " + errorThreshold + "%")
                        .build();
            }

            return builder.build();
        } catch (Exception e) {
            log.error("Error checking Turnstile service health", e);
            return Health.down(e)
                    .withDetail("reason", "Error checking service health: " + e.getMessage())
                    .build();
        }
    }
}
