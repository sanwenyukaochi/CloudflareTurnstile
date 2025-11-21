package io.turnstile.cloudflare.filter;

import io.turnstile.cloudflare.TurnstileConfiguration;
import io.turnstile.cloudflare.service.TurnstileValidationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TurnstileCaptchaFilter extends OncePerRequestFilter {

    Logger logger = LoggerFactory.getLogger(TurnstileConfiguration.class);
    
    private final TurnstileValidationService validationService;

    @Value("${io.turnstile.cloudflare.login.submissionPath:/login}")
    private String loginSubmissionPath;

    @Value("${io.turnstile.cloudflare.login.redirectUrl:/login?error=captcha}")
    private String loginRedirectUrl;

    @Value("${io.turnstile.cloudflare.token.parameterName:cf-turnstile-response}")
    private String turnstileTokenParameterName;

    public TurnstileCaptchaFilter(TurnstileValidationService validationService) {
        this.validationService = validationService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals(loginSubmissionPath) && "POST".equalsIgnoreCase(request.getMethod())) {
            String token = request.getParameter(turnstileTokenParameterName);
            boolean valid = validationService.validateTurnstileResponse(token, getClientIp(request));
            if (valid) {
                filterChain.doFilter(request, response);
            } else {
                logger.warn("Turnstile captcha validation failed for request: {}", request.getServletPath());
                response.sendRedirect(loginRedirectUrl);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        // Delegate to the service method or use a similar logic
        return validationService.getClientIpAddress(request);
    }
}
