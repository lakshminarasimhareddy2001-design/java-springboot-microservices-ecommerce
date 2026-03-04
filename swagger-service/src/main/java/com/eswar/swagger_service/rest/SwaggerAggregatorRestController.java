package com.eswar.swagger_service.rest;

import com.eswar.swagger_service.service.SwaggerAggregationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v3/api-docs")
@RequiredArgsConstructor
public class SwaggerAggregatorRestController {

    private final SwaggerAggregationService aggregationService;


    @GetMapping("/{serviceName}")
    public ResponseEntity<String> getSwagger(@PathVariable String serviceName) {
        String swaggerJson = aggregationService.getSwaggerJson(serviceName);
        return ResponseEntity.ok(swaggerJson);
    }
}
