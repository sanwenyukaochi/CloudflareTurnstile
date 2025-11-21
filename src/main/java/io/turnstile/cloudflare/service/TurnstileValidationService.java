package io.turnstile.cloudflare.service;

import io.turnstile.cloudflare.config.TurnstileConfigProperties;
import io.turnstile.cloudflare.dto.TurnstileResponse;
import io.turnstile.cloudflare.dto.ValidationResult;
import io.turnstile.cloudflare.dto.ValidationResult.ValidationResultType;
import io.turnstile.cloudflare.exception.TurnstileConfigurationException;
import io.turnstile.cloudflare.exception.TurnstileNetworkException;
import io.turnstile.cloudflare.exception.TurnstileValidationException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@Service
public class TurnstileValidationService {
    
    Logger logger = LoggerFactory.getLogger(TurnstileValidationService.class);
    private static final String UNKNOWN = "unknown";
    private static final int MIN_TOKEN_LENGTH = 20;

    private final RestClient turnstileRestClient;
    private final TurnstileConfigProperties properties;

    // Metrics (used regardless of whether Micrometer is available)
    private final LongAdder validationCount = new LongAdder();
    private final LongAdder successCount = new LongAdder();
    private final LongAdder errorCount = new LongAdder();
    private final LongAdder networkErrorCount = new LongAdder();
    private final LongAdder configErrorCount = new LongAdder();
    private final LongAdder validationErrorCount = new LongAdder();
    private final LongAdder inputErrorCount = new LongAdder();
    private final AtomicLong lastResponseTime = new AtomicLong();
    private final AtomicLong totalResponseTime = new AtomicLong();
    private final AtomicLong responseCount = new AtomicLong();

    public TurnstileValidationService(@Qualifier("turnstileRestClient") RestClient turnstileRestClient, TurnstileConfigProperties properties) {
        this.turnstileRestClient = turnstileRestClient;
        this.properties = properties;

    }

    @PostConstruct
    public void onStartup() {
        logger.info("TurnstileValidationService started");
        logger.info("Turnstile URL: {}", properties.getUrl());
        logger.info("Turnstile Sitekey: {}", properties.getSiteKey());
        logger.info("Turnstile Secret: {}", properties.getSecret() != null && !properties.getSecret().isBlank() ? "[CONFIGURED]" : "[NOT CONFIGURED]");
        logger.info("Turnstile Metrics enabled: {}", properties.getMetrics().isEnabled());
        logger.info("Turnstile Health Check enabled: {}", properties.getMetrics().isHealthCheckEnabled());

        // Validate required configuration
        if (properties.getSecret() == null || properties.getSecret().isBlank()) {
            logger.error("Turnstile secret key is not configured. Validation will fail.");
        }

        if (properties.getUrl() == null || properties.getUrl().isBlank()) {
            logger.error("Turnstile URL is not configured. Validation will fail.");
        }
    }

    public boolean validateTurnstileResponse(String token) {
        return validateTurnstileResponse(token, null);
    }

    public boolean validateTurnstileResponse(String token, String remoteIp) {
        try {
            ValidationResult result = validateTurnstileResponseDetailed(token, remoteIp);
            return result.isSuccess();
        } catch (Exception e) {
            logger.error("Unexpected error during Turnstile validation: {}", e.getMessage(), e);
            return false;
        }
    }

    public ValidationResult validateTurnstileResponseDetailed(String token) {
        return validateTurnstileResponseDetailed(token, null);
    }

    public ValidationResult validateTurnstileResponseDetailed(String token, String remoteIp) {
        // Start tracking metrics for this validation attempt
        long startTime = System.currentTimeMillis();
        validationCount.increment();


        logger.trace("Starting validation for token: {} with remoteIp: {}", token, remoteIp);

        // Validate input parameters
        if (token == null) {
            logger.warn("Turnstile validation failed: token cannot be null");
            recordError(ValidationResultType.INPUT_ERROR);
            return ValidationResult.inputError("Token cannot be null");
        }

        if (token.isBlank()) {
            logger.warn("Turnstile validation failed: token cannot be empty or blank");
            recordError(ValidationResultType.INPUT_ERROR);
            return ValidationResult.inputError("Token cannot be empty or blank");
        }

        // Basic format validation - Cloudflare tokens typically start with '0.' or '1.' followed by alphanumeric chars
        // and should be reasonably sized (typically 100+ chars)
        if (token.length() < MIN_TOKEN_LENGTH) {
            logger.warn("Turnstile validation failed: token appears to be too short to be valid (length: {})", token.length());
            recordError(ValidationResultType.INPUT_ERROR);
            return ValidationResult.inputError("Token is too short to be valid (length: " + token.length() + ")");
        }

        // Validate remoteIp if provided
        String cleanRemoteIp = remoteIp;
        if (cleanRemoteIp != null && (cleanRemoteIp.isBlank())) {
            logger.warn("Turnstile validation: ignoring empty or blank remoteIp");
            cleanRemoteIp = null;
        }

        // Validate that we have the required configuration
        if (properties.getSecret() == null || properties.getSecret().isBlank()) {
            String msg = "Turnstile secret key is not configured";
            logger.error(msg);
            recordError(ValidationResultType.CONFIGURATION_ERROR);
            throw new TurnstileConfigurationException(msg);
        }

        if (properties.getUrl() == null || properties.getUrl().isBlank()) {
            String msg = "Turnstile URL is not configured";
            logger.error(msg);
            recordError(ValidationResultType.CONFIGURATION_ERROR);
            throw new TurnstileConfigurationException(msg);
        }

        // Create a JSON request body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("secret", properties.getSecret());
        requestBody.put("response", token);
        Optional.ofNullable(cleanRemoteIp).ifPresent(ip -> requestBody.put("remoteip", ip));

        logger.trace("Making request to Cloudflare Turnstile API at: {}", properties.getUrl());

        // Make the request to Cloudflare's Turnstile API
        try {
            // Use timer if available

                return executeValidationRequest(requestBody, startTime);
            
        } catch (HttpClientErrorException e) {
            // 4xx response status codes (client errors)
            logger.error("Client error during Turnstile validation: {}", e.getMessage(), e);
            recordError(ValidationResultType.NETWORK_ERROR);
            throw new TurnstileNetworkException("Client error: " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            // 5xx response status codes (server errors)
            logger.error("Server error during Turnstile validation: {}", e.getMessage(), e);
            recordError(ValidationResultType.NETWORK_ERROR);
            throw new TurnstileNetworkException("Server error: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            // Network-related exceptions (timeouts, connection errors, etc.)
            logger.error("Network error during Turnstile validation: {}", e.getMessage(), e);
            recordError(ValidationResultType.NETWORK_ERROR);
            throw new TurnstileNetworkException("Network error: " + e.getMessage(), e);
        } catch (TurnstileValidationException e) {
            // Re-throw the TurnstileValidationException
            recordError(ValidationResultType.INVALID_TOKEN);
            throw e;
        } catch (Exception e) {
            // Catch-all for any other unexpected exceptions
            logger.error("Unexpected error during Turnstile validation: {}", e.getMessage(), e);
            recordError(ValidationResultType.NETWORK_ERROR);
            throw new TurnstileNetworkException("Unexpected error: " + e.getMessage(), e);
        }
    }

    private ValidationResult executeValidationRequest(Map<String, String> requestBody, long startTime) {
        TurnstileResponse response = turnstileRestClient.post().uri(properties.getUrl())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body(requestBody).retrieve().body(TurnstileResponse.class);

        // Record response time
        long responseTime = System.currentTimeMillis() - startTime;
        lastResponseTime.set(responseTime);
        totalResponseTime.addAndGet(responseTime);
        responseCount.incrementAndGet();

        logger.debug("Turnstile response: {} (took {}ms)", response, responseTime);

        if (response == null) {
            logger.warn("Turnstile API returned null response");
            recordError(ValidationResultType.NETWORK_ERROR);
            return ValidationResult.networkError("Cloudflare returned an empty response");
        }

        if (response.isSuccess()) {
            logger.debug("Turnstile validation successful");
            successCount.increment();

            return ValidationResult.success();
        } else {
            logger.warn("Turnstile validation failed with error codes: {}", response.getErrorCodes());
            recordError(ValidationResultType.INVALID_TOKEN);
            throw new TurnstileValidationException("Token validation failed", response.getErrorCodes());
        }
    }

    private void recordError(ValidationResultType resultType) {
        errorCount.increment();
        switch (resultType) {
            case NETWORK_ERROR:
                networkErrorCount.increment();

                break;
            case CONFIGURATION_ERROR:
                configErrorCount.increment();

                break;
            case INVALID_TOKEN:
                validationErrorCount.increment();

                break;
            case INPUT_ERROR:
                inputErrorCount.increment();

                break;
            default:
                // No specific counter for SUCCESS or other types
                break;
        }
    }

    public long getValidationCount() {
        return validationCount.sum();
    }

    public long getSuccessCount() {
        return successCount.sum();
    }

    public long getErrorCount() {
        return errorCount.sum();
    }

    public long getNetworkErrorCount() {
        return networkErrorCount.sum();
    }

    public long getConfigErrorCount() {
        return configErrorCount.sum();
    }

    public long getValidationErrorCount() {
        return validationErrorCount.sum();
    }

    public long getInputErrorCount() {
        return inputErrorCount.sum();
    }

    public long getLastResponseTime() {
        return lastResponseTime.get();
    }

    public double getAverageResponseTime() {
        long count = responseCount.get();
        return count > 0 ? (double) totalResponseTime.get() / count : 0;
    }

    public double getErrorRate() {
        long total = validationCount.sum();
        return total > 0 ? (double) errorCount.sum() * 100 / total : 0;
    }

    public String getTurnstileSiteKey() {
        return properties.getSiteKey();
    }

    public String getClientIpAddress(ServletRequest request) {
        if (request instanceof HttpServletRequest httpRequest) {
            String[] headers = {"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
            for (String header : headers) {
                String ipHeaderValue = httpRequest.getHeader(header);
                if (ipHeaderValue == null || ipHeaderValue.isBlank()) {
                    continue;
                }

                String candidate = ipHeaderValue.split(",", 2)[0].trim();
                if (!candidate.isEmpty() && !UNKNOWN.equalsIgnoreCase(candidate)) {
                    return candidate;
                }
            }
        }
        return request.getRemoteAddr();
    }
}
