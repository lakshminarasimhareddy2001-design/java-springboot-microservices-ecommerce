package com.eswar.gatewayservice.handler;

import ch.qos.logback.core.spi.ErrorCodes;
import com.eswar.gatewayservice.exceptions.BusinessException;
import com.eswar.gatewayservice.exceptions.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.eswar.gatewayservice.exceptions.ErrorCode.*;

@Component
@Slf4j
@Order(-2)
public  class gatewayReactiveExceptionHandler implements ErrorWebExceptionHandler {
    @Override
    public @NonNull Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {

        ServerRequest request = ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());

        return renderErrorResponse(request, ex)
                .flatMap(response -> response.writeTo(exchange, new DefaultServerResponseContext()));
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request, Throwable ex) {

       HttpStatus httpStatus=HttpStatus.INTERNAL_SERVER_ERROR;
         if(ex instanceof BusinessException be){
              httpStatus=be.getStatus();
         }
        Map<String, Object> errorMap = new LinkedHashMap<>();
        errorMap.put("title", ex instanceof BusinessException ? "Business Error" : "Internal Error");
        errorMap.put("status", httpStatus.value());
        errorMap.put("detail", ex.getMessage());
        errorMap.put("instance", request.path());
        errorMap.put("timestamp", Instant.now());
        errorMap.put("errorCode", ex instanceof BusinessException ? ((BusinessException) ex).getErrorCode() : "INTERNAL_ERROR");

        log.error("Exception caught in GlobalReactiveExceptionHandler: {}", ex.getMessage(), ex);

        return ServerResponse.status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorMap));
    }

    private HttpStatus determineHttpStatus(Throwable ex) {
        if (ex instanceof BusinessException bex) {
            return bex.getStatus();
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private static class DefaultServerResponseContext implements ServerResponse.Context {
        @Override
        public List<HttpMessageWriter<?>> messageWriters() {
            return HandlerStrategies.withDefaults().messageWriters();
        }

        @Override
        public List<ViewResolver> viewResolvers() {
            return List.of();
        }
    }
}
