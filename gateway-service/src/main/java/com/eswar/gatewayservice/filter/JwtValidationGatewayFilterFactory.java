package com.eswar.gatewayservice.filter;

import com.eswar.gatewayservice.util.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

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

            //skip preflight
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequest().getMethod().name())) {
                return chain.filter(exchange);
            }
            // 1️⃣ Extract Authorization header
            List<String> authHeaders = exchange.getRequest().getHeaders().getOrEmpty("Authorization");
            if (authHeaders.isEmpty()) {
                exchange.getResponse().setStatusCode(UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }



            String authHeader = authHeaders.getFirst();
            //formate issue
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }


            String token = authHeader.substring(7);

            // ✅ Validate token
            if (!jwtService.isTokenValid(token)) {
                exchange.getResponse().setStatusCode(UNAUTHORIZED);
                return exchange.getResponse().setComplete();
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
            log.info("Request get for JWTValidation and completed: {}",exchange.getRequest().getPath().toString());
            return chain.filter(modifiedExchange);

        };
    }

    public static class Config {
        // Add fields if needed (header names, secret keys, etc.)
    }
}