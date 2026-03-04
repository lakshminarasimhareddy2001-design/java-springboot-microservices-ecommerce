package com.eswar.swagger_service.service;

import com.eswar.swagger_service.config.SwaggerServiceConfig;
import com.eswar.swagger_service.model.SwaggerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class SwaggerAggregationService {

    private final SwaggerServiceConfig swaggerServiceConfig;
    private final RestTemplate restTemplate;


    public String getSwaggerJson(String serviceName) {
        SwaggerService service = swaggerServiceConfig.getServices().stream()
                .filter(s-> s.getName().toLowerCase().contains(serviceName.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Service not found: " + serviceName));

        return restTemplate.getForObject(service.getUrl(), String.class);
    }
}
