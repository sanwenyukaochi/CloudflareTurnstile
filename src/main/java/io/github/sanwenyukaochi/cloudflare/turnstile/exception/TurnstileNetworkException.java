package io.github.sanwenyukaochi.cloudflare.turnstile.exception;

/**
 * 当 Turnstile 验证期间发生网络相关问题时抛出异常。
 * <p>
 * 此异常表明与 Cloudflare Turnstile API 通信时出现连接超时、DNS 解析失败或其他网络相关问题。
 * Cloudflare Turnstile API。
 * </p>
 */
public class TurnstileNetworkException extends TurnstileException {

    /**
     * 使用指定的详细信息消息构造一个新的 Turnstile 网络异常。
     *
     * @param message 详细信息消息
     */
    public TurnstileNetworkException(String message) {
        super(message);
    }

    /**
     * 构造一个新的 Turnstile 网络异常，并指定异常详情和原因。
     *
     * @param message 异常详情
     * @param cause 异常原因
     */
    public TurnstileNetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
