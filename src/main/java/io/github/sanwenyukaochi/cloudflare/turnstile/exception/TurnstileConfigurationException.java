package io.github.sanwenyukaochi.cloudflare.turnstile.exception;

/**
 * 当 Turnstile 配置出现问题时抛出异常。
 * <p>
 * 此异常表明 Turnstile 服务的配置存在问题，例如缺少或无效的密钥、URL 或其他必需的配置属性。
 * </p>
 */
public class TurnstileConfigurationException extends TurnstileException {

    /**
     * 使用指定的详细信息消息构造一个新的旋转闸门配置异常。
     *
     * @param message 详细信息消息
     */
    public TurnstileConfigurationException(String message) {
        super(message);
    }

    /**
     * 构造一个新的旋转闸机配置异常，并指定异常详情和原因。
     *
     * @param message 异常详情
     * @param cause 异常原因
     */
    public TurnstileConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
