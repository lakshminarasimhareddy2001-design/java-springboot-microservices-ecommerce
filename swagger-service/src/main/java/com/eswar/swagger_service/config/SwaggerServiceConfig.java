package com.eswar.swagger_service.config;

import com.eswar.swagger_service.model.SwaggerService;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "swagger")
@Data
public class SwaggerServiceConfig {
    private List<SwaggerService> services;
}
