package com.eswar.inventoryservice.config;

import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/v3/api-docs"
    };
    private static final String[] ACTUATOR_WHITELIST = {
            "/actuator/health",
            "/actuator/info"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(@NonNull HttpSecurity httpSecurity){



        return  httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        request -> request


                                .requestMatchers(
                                        SWAGGER_WHITELIST
                                ).permitAll()
                                .requestMatchers(
                                        ACTUATOR_WHITELIST
                                ).permitAll()
                                // Public product view
                                .requestMatchers(HttpMethod.GET, "/api/v1/inventory").permitAll()
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                // Admin only
                                .requestMatchers(HttpMethod.POST, "/api/v1/inventory").permitAll()


                                .anyRequest().authenticated()


                )
                .build();


    }
}
