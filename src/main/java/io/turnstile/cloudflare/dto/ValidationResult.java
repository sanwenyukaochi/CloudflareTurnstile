package io.turnstile.cloudflare.dto;

import java.util.Collections;
import java.util.List;

public final class ValidationResult {

    private final boolean success;
    private final List<String> errorCodes;
    private final String message;
    private final ValidationResultType resultType;

    private ValidationResult(boolean success, List<String> errorCodes, String message, ValidationResultType resultType) {
        this.success = success;
        this.errorCodes = errorCodes != null ? Collections.unmodifiableList(errorCodes) : Collections.emptyList();
        this.message = message;
        this.resultType = resultType;
    }

    public static ValidationResult success() {
        return new ValidationResult(true, Collections.emptyList(), "Validation successful", ValidationResultType.SUCCESS);
    }

    public static ValidationResult invalidToken(List<String> errorCodes) {
        return new ValidationResult(false, errorCodes, "Token validation failed", ValidationResultType.INVALID_TOKEN);
    }

    public static ValidationResult networkError(String errorMessage) {
        return new ValidationResult(false, Collections.emptyList(), "Network error during validation: " + errorMessage,
                ValidationResultType.NETWORK_ERROR);
    }

    public static ValidationResult configurationError(String errorMessage) {
        return new ValidationResult(false, Collections.emptyList(), "Configuration error: " + errorMessage, ValidationResultType.CONFIGURATION_ERROR);
    }

    public static ValidationResult inputError(String errorMessage) {
        return new ValidationResult(false, Collections.emptyList(), "Input validation error: " + errorMessage, ValidationResultType.INPUT_ERROR);
    }

    public boolean isSuccess() {
        return success;
    }

    public List<String> getErrorCodes() {
        return errorCodes;
    }

    public String getMessage() {
        return message;
    }

    public ValidationResultType getResultType() {
        return resultType;
    }

    public enum ValidationResultType {
        SUCCESS,
        INVALID_TOKEN,
        NETWORK_ERROR,
        CONFIGURATION_ERROR,
        INPUT_ERROR
    }
}
