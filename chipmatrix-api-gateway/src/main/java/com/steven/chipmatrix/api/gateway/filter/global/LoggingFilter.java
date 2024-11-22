package com.steven.chipmatrix.api.gateway.filter.global;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();
        
        // 记录请求开始时间
        long startTime = System.currentTimeMillis();
        
        // 收集请求信息
        List<String> requestInfo = new ArrayList<>();
        requestInfo.add(String.format("Method: %s", method));
        requestInfo.add(String.format("Path: %s", path));
        requestInfo.add(String.format("Headers: %s", request.getHeaders()));
        requestInfo.add(String.format("Query Parameters: %s", request.getQueryParams()));
        
        log.info("Incoming Request: {}", String.join(", ", requestInfo));

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    
                    // 收集响应信息
                    List<String> responseInfo = new ArrayList<>();
                    responseInfo.add(String.format("Status: %s", exchange.getResponse().getStatusCode()));
                    responseInfo.add(String.format("Duration: %dms", duration));
                    responseInfo.add(String.format("Headers: %s", exchange.getResponse().getHeaders()));
                    
                    log.info("Outgoing Response: {}", String.join(", ", responseInfo));
                }));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE; // 最后执行，这样可以记录完整的请求处理过程
    }
}