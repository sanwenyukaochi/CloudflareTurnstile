package io.github.sanwenyukaochi.cloudflare.turnstile.exception;

/**
 * 所有与旋转闸机相关的异常的基类。
 * <p>
 * 此异常类是所有特定旋转闸机异常的父类。它提供通用功能，并允许在需要时使用单个 catch 块捕获所有
 * 旋转闸机异常。
 * </p>
 */
public class TurnstileException extends RuntimeException {

    /**
     * 构造一个新的 Turnstile 异常，并指定详细信息。
     *
     * @param message 详细信息
     */
    public TurnstileException(String message) {
        super(message);
    }

    /**
     * 构造一个新的 Turnstile 异常，并指定详细信息和原因。
     *
     * @param message 详细信息
     * @param cause 异常原因
     */
    public TurnstileException(String message, Throwable cause) {
        super(message, cause);
    }
}
