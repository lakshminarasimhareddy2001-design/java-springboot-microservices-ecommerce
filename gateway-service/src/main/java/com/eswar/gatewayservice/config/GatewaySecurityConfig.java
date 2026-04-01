package com.eswar.gatewayservice.config;

import com.eswar.gatewayservice.handler.GatewayAccessDeniedHandler;
import com.eswar.gatewayservice.handler.GatewayAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@Configuration
@RequiredArgsConstructor
public class GatewaySecurityConfig {


    private final GatewayAuthenticationEntryPoint authEntryPoint;
    private final GatewayAccessDeniedHandler accessDeniedHandler;



    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/admin/**").hasRole("ADMIN")  // only ADMIN
                        .pathMatchers("/users/**").authenticated() // any authenticated user
                        .anyExchange().permitAll()                  // everything else allowed anonymously
                )
                .exceptionHandling( ex->ex
                .authenticationEntryPoint(authEntryPoint)
                .accessDeniedHandler(accessDeniedHandler))
                .build();
    }
}
