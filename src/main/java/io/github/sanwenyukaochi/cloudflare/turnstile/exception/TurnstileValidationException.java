package io.github.sanwenyukaochi.cloudflare.turnstile.exception;

import java.util.Collections;
import java.util.List;
import lombok.Getter;

/**
 *
 * 当 Cloudflare Turnstile API 明确拒绝令牌时抛出的异常。
 * <p>
 * 此异常表明 Cloudflare Turnstile API 已成功处理请求，但拒绝了该令牌，认为其无效。此异常中包含 Cloudflare 返回的错误代码。
 * </p>
 */
@Getter
public class TurnstileValidationException extends TurnstileException {

    /**
     * Cloudflare 返回的错误代码列表。
     * -- GETTER --
     * 获取 Cloudflare Turnstile API 返回的错误代码列表。
     */
    private final List<String> errorCodes;

    /**
     * 使用指定的详细消息和错误代码构造一个新的 Turnstile 验证异常。
     *
     * @param message 详细消息
     * @param errorCodes Cloudflare 返回的错误代码列表
     */
    public TurnstileValidationException(String message, List<String> errorCodes) {
        super(message);
        this.errorCodes = errorCodes != null ? Collections.unmodifiableList(errorCodes) : Collections.emptyList();
    }
}
