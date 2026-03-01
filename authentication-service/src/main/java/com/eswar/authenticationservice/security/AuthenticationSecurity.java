package com.eswar.authenticationservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class AuthenticationSecurity {


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
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){



     return  httpSecurity
             .csrf(AbstractHttpConfigurer::disable)
             .authorizeHttpRequests(
                           request -> request


                                   .requestMatchers(
                                           SWAGGER_WHITELIST
                                   ).permitAll()
                                   .requestMatchers(
                                           ACTUATOR_WHITELIST
                                   ).permitAll()
                                   .requestMatchers(
                                          "/api/v1/auth/login"
                                   ).permitAll()
                                   .anyRequest().permitAll()

                   )
             .httpBasic(AbstractHttpConfigurer::disable) // optional, disable basic auth
             .formLogin(AbstractHttpConfigurer::disable) // disable redirect login page
             .build();


    }
}
