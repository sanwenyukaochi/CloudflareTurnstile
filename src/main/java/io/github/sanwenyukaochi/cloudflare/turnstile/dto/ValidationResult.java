package io.github.sanwenyukaochi.cloudflare.turnstile.dto;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 表示 Turnstile 校验操作的结果。
 * <p>
 * 该类提供了 Turnstile 令牌校验结果的详细信息，包括校验是否成功、
 * Cloudflare 返回的错误码，以及与本次校验相关的附加上下文信息。
 * </p>
 */
@Getter
public final class ValidationResult {

    /**
     * -- GETTER --
     *  返回校验是否成功。
     */
    private final boolean success;

    /**
     * -- GETTER --
     *  返回校验失败时的错误码列表。
     *
     */
    private final List<String> errorCodes;

    /**
     * -- GETTER --
     *  返回描述本次校验结果的消息。
     */
    private final String message;

    /**
     * -- GETTER --
     *  返回校验结果的类型。
     */
    private final ValidationResultType resultType;

    /**
     * ValidationResult 的私有构造方法，请通过静态工厂方法创建实例。
     *
     * @param success 校验是否成功
     * @param errorCodes 校验失败时的错误码列表
     * @param message 本次校验结果的描述信息
     * @param resultType 校验结果类型
     */
    private ValidationResult(
            boolean success, List<String> errorCodes, String message, ValidationResultType resultType) {
        this.success = success;
        this.errorCodes = errorCodes != null ? Collections.unmodifiableList(errorCodes) : Collections.emptyList();
        this.message = message;
        this.resultType = resultType;
    }

    /**
     * 创建一个表示校验成功的结果对象。
     *
     * @return 表示校验成功的 ValidationResult
     */
    public static ValidationResult success() {
        return new ValidationResult(
                true, Collections.emptyList(), "Validation successful", ValidationResultType.SUCCESS);
    }

    /**
     * 创建一个表示 Cloudflare 返回令牌无效的结果对象。
     *
     * @param errorCodes Cloudflare 返回的错误码列表
     * @return 表示令牌无效的 ValidationResult
     */
    public static ValidationResult invalidToken(List<String> errorCodes) {
        return new ValidationResult(false, errorCodes, "Token validation failed", ValidationResultType.INVALID_TOKEN);
    }

    /**
     * 创建一个表示网络异常的结果对象。
     *
     * @param errorMessage 描述网络问题的错误信息
     * @return 表示网络异常的 ValidationResult
     */
    public static ValidationResult networkError(String errorMessage) {
        return new ValidationResult(
                false,
                Collections.emptyList(),
                "Network error during validation: " + errorMessage,
                ValidationResultType.NETWORK_ERROR);
    }

    /**
     * 创建一个表示配置异常的结果对象。
     *
     * @param errorMessage 描述配置问题的错误信息
     * @return 表示配置异常的 ValidationResult
     */
    public static ValidationResult configurationError(String errorMessage) {
        return new ValidationResult(
                false,
                Collections.emptyList(),
                "Configuration error: " + errorMessage,
                ValidationResultType.CONFIGURATION_ERROR);
    }

    /**
     * 创建一个表示输入校验异常的结果对象。
     *
     * @param errorMessage 描述输入校验问题的错误信息
     * @return 表示输入校验异常的 ValidationResult
     */
    public static ValidationResult inputError(String errorMessage) {
        return new ValidationResult(
                false,
                Collections.emptyList(),
                "Input validation error: " + errorMessage,
                ValidationResultType.INPUT_ERROR);
    }

    /**
     * 表示不同校验结果类型的枚举。
     */
    @RequiredArgsConstructor
    public enum ValidationResultType {
        /** 校验成功。 */
        SUCCESS,

        /** 根据 Cloudflare 的校验结果，令牌无效。 */
        INVALID_TOKEN,

        /** 校验过程中发生网络异常。 */
        NETWORK_ERROR,

        /** 检测到配置异常。 */
        CONFIGURATION_ERROR,

        /** 发生输入校验异常。 */
        INPUT_ERROR,
        ;

    }
}