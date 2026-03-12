package io.github.sanwenyukaochi.cloudflare.turnstile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Cloudflare Turnstile 校验响应。
 * <p>
 * 该对象用于映射 Turnstile 服务端校验接口返回的 JSON 数据，
 * 包含校验结果、挑战完成时间、挑战完成时的主机名以及错误码列表。
 * </p>
 *
 * @param success     校验是否成功
 * @param challengeTs 挑战完成的时间戳
 * @param hostname    挑战完成所在站点的主机名
 * @param errorCodes  接口返回的错误码列表
 *
 * @see <a href="https://developers.cloudflare.com/turnstile/get-started/server-side-validation/">
 * Cloudflare Turnstile 服务端校验文档
 * </a>
 */
public record TurnstileResponse(
        @JsonProperty("success") boolean success,
        @JsonProperty("challenge_ts") String challengeTs,
        @JsonProperty("hostname") String hostname,
        @JsonProperty("error-codes") List<String> errorCodes) {}
