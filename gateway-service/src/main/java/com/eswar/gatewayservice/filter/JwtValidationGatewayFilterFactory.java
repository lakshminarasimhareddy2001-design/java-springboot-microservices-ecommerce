package com.eswar.gatewayservice.filter;

import com.eswar.gatewayservice.exceptions.BusinessException;
import com.eswar.gatewayservice.exceptions.ErrorCode;
import com.eswar.gatewayservice.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component("JwtValidation")
@Slf4j
public class JwtValidationGatewayFilterFactory
        extends AbstractGatewayFilterFactory<JwtValidationGatewayFilterFactory.Config> {

    private final JwtService jwtService;

    public JwtValidationGatewayFilterFactory(JwtService jwtService) {
        super(Config.class);
        this.jwtService = jwtService;
    }

    @Override
    public @NonNull GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            log.info("JWT Filter HIT: {}", exchange.getRequest().getPath());

            // Skip preflight OPTIONS requests
            if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
                return chain.filter(exchange);
            }

            // 1️⃣ Extract Authorization header
            List<String> authHeaders = exchange.getRequest().getHeaders().getOrEmpty("Authorization");
            if (authHeaders.isEmpty()) {
                return unauthorized(exchange, "Authorization header is missing");
            }

            String authHeader = authHeaders.getFirst();
            if (!authHeader.startsWith("Bearer ")) {
                return unauthorized(exchange, "Invalid Authorization header format");
            }

            String token = authHeader.substring(7);

            // 2️⃣ Validate token
            try {
                jwtService.validateToken(token); // already throws BusinessException
            } catch (BusinessException ex) {
                // propagate to reactive exception handler
                return Mono.error(ex);
            }

            // 3️⃣ Extract user info
            String userId = jwtService.extractUserId(token);
            String email = jwtService.extractUserEmail(token);
            List<String> roles = jwtService.extractUserRoles(token);

            // 4️⃣ Add headers for downstream services
            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(r -> r.headers(headers -> {
                        headers.add("X-User-Id", userId);
                        headers.add("X-User-Email", email);
                        headers.add("X-User-Roles", String.join(",", roles));
                    }))
                    .build();

            log.info("JWT validation completed for request: {}", exchange.getRequest().getPath());
            return chain.filter(modifiedExchange);
        };
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        log.warn("Unauthorized access: {}", message);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
        // Add fields if needed (header names, secret keys, etc.)
    }
}
