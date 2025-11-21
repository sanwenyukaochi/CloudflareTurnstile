package io.turnstile.cloudflare.exception;

public class TurnstileNetworkException extends TurnstileException {

    public TurnstileNetworkException(String message) {
        super(message);
    }

    public TurnstileNetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
