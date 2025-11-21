package io.turnstile.cloudflare.exception;

import java.util.Collections;
import java.util.List;

public class TurnstileValidationException extends TurnstileException {

    private final List<String> errorCodes;

    public TurnstileValidationException(String message, List<String> errorCodes) {
        super(message);
        this.errorCodes = errorCodes != null ? Collections.unmodifiableList(errorCodes) : Collections.emptyList();
    }

    public List<String> getErrorCodes() {
        return errorCodes;
    }
}
