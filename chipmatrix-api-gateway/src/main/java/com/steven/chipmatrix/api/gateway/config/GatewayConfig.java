package com.steven.chipmatrix.api.gateway.config;

import com.steven.chipmatrix.api.gateway.handler.HystrixFallbackHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * 网关配置
 * 包含路由配置、限流配置、降级配置等
 */
@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final HystrixFallbackHandler hystrixFallbackHandler;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 认证服务路由
                .route("auth-service", r -> r.path("/auth/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(ipKeyResolver()))
                                .circuitBreaker(config -> config
                                        .setName("authFallback")
                                        .setFallbackUri("forward:/fallback"))
                                .retry(config -> config
                                        .setRetries(3)
                                        .setStatuses(HttpStatus.BAD_GATEWAY)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .hystrix(config -> config
                                        .setName("authHystrix")
                                        .setFallbackUri("forward:/fallback")))
                        .uri("lb://chipmatrix-auth"))
                
                // 系统服务路由
                .route("system-service", r -> r.path("/system/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(ipKeyResolver()))
                                .circuitBreaker(config -> config
                                        .setName("systemFallback")
                                        .setFallbackUri("forward:/fallback"))
                                .retry(config -> config
                                        .setRetries(3)
                                        .setStatuses(HttpStatus.BAD_GATEWAY)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .hystrix(config -> config
                                        .setName("systemHystrix")
                                        .setFallbackUri("forward:/fallback")))
                        .uri("lb://chipmatrix-system"))
                
                // 文件服务路由
                .route("file-service", r -> r.path("/file/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .requestSize(5242880L) // 5MB
                                .retry(3))
                        .uri("lb://chipmatrix-file"))
                
                // 监控服务路由
                .route("monitor-service", r -> r.path("/monitor/**")
                        .and()
                        .method("GET")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://chipmatrix-monitor"))
                
                // WebSocket路由
                .route("websocket-route", r -> r.path("/ws/**")
                        .uri("lb:ws://chipmatrix-websocket"))
                
                .build();
    }

    /**
     * 降级处理路由
     */
    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions.route()
                .GET("/fallback", hystrixFallbackHandler)
                .POST("/fallback", hystrixFallbackHandler)
                .build();
    }

    /**
     * Redis限流配置
     * replenishRate: 令牌桶每秒填充平均速率
     * burstCapacity: 令牌桶总容量
     */
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(10, 20);
    }

    /**
     * 限流key解析器
     * 支持按IP限流
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(
                exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
        );
    }

    /**
     * 用户限流key解析器
     * 支持按用户限流
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.justOrEmpty(
                exchange.getRequest().getHeaders().getFirst("X-User-Id")
        ).defaultIfEmpty("anonymous");
    }

    /**
     * 接口限流key解析器
     * 支持按接口路径限流
     */
    @Bean
    public KeyResolver apiKeyResolver() {
        return exchange -> Mono.just(
                exchange.getRequest().getPath().value()
        );
    }
}