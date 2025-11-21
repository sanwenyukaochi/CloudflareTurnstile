package io.turnstile.cloudflare.exception;

public class TurnstileConfigurationException extends TurnstileException {

    public TurnstileConfigurationException(String message) {
        super(message);
    }

    public TurnstileConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
