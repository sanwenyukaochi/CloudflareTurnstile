
package io.turnstile.cloudflare;

import io.turnstile.cloudflare.service.TurnstileValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@Controller
public class LoginController {

    private final TurnstileValidationService turnstileValidationService;

    public LoginController(TurnstileValidationService turnstileValidationService) {
        this.turnstileValidationService = turnstileValidationService;
    }

//    @PostMapping("/login")
//    public ResponseEntity<Map<String, Object>> login(
//            @RequestHeader(name = "cf-turnstile-response") String turnstileResponse,
//            HttpServletRequest request) {
//
//        // Get the client IP address (recommended for security)
//        String clientIpAddress = turnstileValidationService.getClientIpAddress(request);
//
//        // Validate the Turnstile response token
//        boolean turnstileValid = turnstileValidationService.validateTurnstileResponse(turnstileResponse, clientIpAddress);
//
//        if (!turnstileValid) {
//            log.warn("Turnstile validation failed for login request from IP: {}", clientIpAddress);
//
//            return ResponseEntity.status(400).body(Map.of(
//                    "success_flag", false,
//                    "error", "Turnstile validation failed"
//            ));
//        }
//
//        return ResponseEntity.ok(Map.of(
//                "success_flag", true
//        ));
//    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(HttpServletRequest request) {
        return ResponseEntity.ok(Map.of("success_flag", true));
    }

}