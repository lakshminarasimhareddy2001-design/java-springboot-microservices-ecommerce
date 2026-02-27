package com.eswar.userservice.dto;

import lombok.Builder;

import java.time.Instant;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

@Builder
public record ErrorResponse(
        Instant timestamp,
         int status,
         String error,
         String message,
         String path

) {
}
