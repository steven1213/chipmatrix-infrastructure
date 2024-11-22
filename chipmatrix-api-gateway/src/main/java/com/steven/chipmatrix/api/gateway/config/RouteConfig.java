package com.steven.chipmatrix.api.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("chipmatrix-auth", r -> r.path("/auth/**")
                        .uri("lb://chipmatrix-auth"))
                .route("chipmatrix-system", r -> r.path("/system/**")
                        .uri("lb://chipmatrix-system"))
                .build();
    }
}
