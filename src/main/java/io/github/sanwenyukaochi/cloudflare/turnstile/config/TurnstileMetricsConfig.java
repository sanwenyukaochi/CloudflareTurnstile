package io.github.sanwenyukaochi.cloudflare.turnstile.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.config.MeterFilter;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Turnstile 指标和监控配置。
 * <p>
 * 此类配置 Cloudflare Turnstile 服务的指标。它为与 Turnstile 服务相关的所有指标设置通用标签和过滤器。
 * 仅当 micrometer-core 位于类路径中且配置中启用了指标时，才会配置这些指标。
 * </p>
 */
@Slf4j
@Configuration
@ConditionalOnClass(MeterRegistry.class)
@ConditionalOnProperty(
        prefix = "io.github.sanwenyukaochi.cloudflare.turnstile.metrics",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class TurnstileMetricsConfig {

    /**
     * 自定义闸机指标的计量表注册表。
     * <p>
     * 为所有与闸机相关的指标添加通用标签“component:turnstile”，并为所有闸机指标配置前缀。
     * </p>
     *
     * @return 一个 MeterRegistryCustomizer 对象，用于自定义计量表注册表
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> turnstileMeterRegistryCustomizer() {
        log.info("Configuring Turnstile metrics");
        return registry -> {
            // 为所有闸机指标添加通用标签
            registry.config()
                    .meterFilter(MeterFilter.acceptNameStartsWith("turnstile"))
                    .meterFilter(MeterFilter.commonTags(Collections.singletonList(Tag.of("component", "turnstile"))));
        };
    }
}
