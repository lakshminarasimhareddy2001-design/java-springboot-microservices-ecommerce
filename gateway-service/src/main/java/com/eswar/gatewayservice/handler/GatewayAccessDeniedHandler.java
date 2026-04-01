package com.eswar.gatewayservice.handler;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
public class GatewayAccessDeniedHandler implements ServerAccessDeniedHandler {

    @Override
    public @NonNull Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().add("Content-Type", "application/json");

        String body = "{\"errorCode\":\"ACCESS_DENIED\",\"title\":\"Forbidden\",\"detail\":\""
                + ex.getMessage() + "\",\"timestamp\":\"" + Instant.now() + "\"}";

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }
}
