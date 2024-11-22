package com.steven.chipmatrix.api.gateway.filter.global;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * 请求头过滤器
 * 添加全局请求头，如traceId等
 */
@Slf4j
@Component
public class RequestHeaderFilter implements GlobalFilter, Ordered {

    private static final String TRACE_ID = "X-Trace-Id";
    private static final String REQUEST_TIME = "X-Request-Time";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // 生成请求追踪ID
        String traceId = UUID.randomUUID().toString().replace("-", "");
        
        // 获取请求时间
        String requestTime = String.valueOf(System.currentTimeMillis());

        // 构建新的请求，添加自定义请求头
        ServerHttpRequest newRequest = request.mutate()
                .header(TRACE_ID, traceId)
                .header(REQUEST_TIME, requestTime)
                .build();

        // 打印请求信息
        log.info("Request path: {}, traceId: {}", request.getPath(), traceId);

        return chain.filter(exchange.mutate()
                .request(newRequest)
                .build());
    }

    @Override
    public int getOrder() {
        return -200; // 确保在认证过滤器之前执行
    }
}