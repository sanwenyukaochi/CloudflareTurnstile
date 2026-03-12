package io.github.sanwenyukaochi.cloudflare.turnstile.filter;

import io.github.sanwenyukaochi.cloudflare.turnstile.service.TurnstileValidationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 过滤传入的 HTTP 请求，以验证登录提交期间 Turnstile 验证码令牌。
 * <p>
 * 此过滤器拦截发送到已配置的登录提交路径的 POST 请求，并验证请求中提供的 Turnstile 验证码令牌。如果
 * 令牌有效，则允许请求继续通过过滤器链。如果令牌无效，则将用户重定向到已配置的
 * 错误 URL。
 * </p>
 *
 *
 * 配置属性：
 * <ul>
 * <li><b>io.github.sanwenyukaochi.cloudflare.turnstile.login.submissionPath</b>：用于拦截登录提交的路径（默认值：<code>/login</code>）。</li>
 * <li><b>io.github.sanwenyukaochi.cloudflare.turnstile.login.redirectUrl</b>：验证码验证失败时重定向到的 URL（默认值：
 * <code>/login?error=captcha</code>）。</li>
 * <li><b>io.github.sanwenyukaochi.cloudflare.turnstile.token.parameterName</b>：包含 Turnstile 令牌的请求参数名称（默认值：
 * <code>cf-turnstile-response</code>）。</li>
 * </ul>
 *
 * <p>
 * 注意：会提取客户端 IP 地址。使用 {@link TurnstileValidationService#getClientIpAddress(ServletRequest)} 方法。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TurnstileCaptchaFilter extends OncePerRequestFilter {

    private final TurnstileValidationService validationService;

    @Value("${io.github.sanwenyukaochi.cloudflare.turnstile.login.submissionPath:/login}")
    private String loginSubmissionPath;

    @Value("${io.github.sanwenyukaochi.cloudflare.turnstile.login.redirectUrl:/login?error=captcha}")
    private String loginRedirectUrl;

    @Value("${io.github.sanwenyukaochi.cloudflare.turnstile.token.parameterName:cf-turnstile-response}")
    private String turnstileTokenParameterName;

    /**
     * 过滤传入的 HTTP 请求，以在登录提交期间验证 Turnstile 验证码令牌。
     * <p>
     * 此过滤器拦截发送到已配置的登录提交路径的 POST 请求，并验证请求中提供的 Turnstile 验证码令牌。
     * 如果令牌有效，则允许请求继续通过过滤链。如果令牌无效，则将用户重定向到
     * 已配置的错误 URL。
     * </p>
     *
     * @param request 包含客户端请求的 {@link HttpServletRequest} 对象
     * @param response 包含过滤器发送的响应的 {@link HttpServletResponse} 对象
     * @param filterChain 用于将请求和响应传递给下一个过滤器的 {@link FilterChain}
     * @throws ServletException 如果在过滤过程中发生错误
     * @throws IOException 如果在过滤过程中发生 I/O 错误
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getServletPath().equals(loginSubmissionPath) && !request.getMethod().equals(HttpMethod.POST.name())) {
            String token = obtainTurnstileTokenParameterName(request);
            boolean valid = validationService.validateTurnstileResponse(token, getClientIp(request));
            if (valid) {
                filterChain.doFilter(request, response);
            } else {
                log.warn("Turnstile captcha validation failed for request: {}", request.getServletPath());
                response.sendRedirect(loginRedirectUrl);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        // 委托给服务方法或使用类似逻辑
        return validationService.getClientIpAddress(request);
    }

    @Nullable
    protected String obtainTurnstileTokenParameterName(HttpServletRequest request) {
        return request.getParameter(this.turnstileTokenParameterName);
    }
}
