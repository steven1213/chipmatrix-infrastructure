package com.steven.chipmatrix.api.gateway.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 网关统一异常处理
 */
@Slf4j
@Order(-1)
@Component
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // 设置响应类型为JSON
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        if (ex instanceof ResponseStatusException) {
            response.setStatusCode(((ResponseStatusException) ex).getStatusCode());
        }

        return response
                .writeWith(Mono.fromSupplier(() -> {
                    DataBufferFactory bufferFactory = response.bufferFactory();
                    try {
                        // 构建错误信息
                        Map<String, Object> result = new HashMap<>(4);
                        result.put("code", response.getStatusCode().value());
                        result.put("message", ex.getMessage());
                        result.put("path", exchange.getRequest().getPath().value());
                        result.put("timestamp", System.currentTimeMillis());

                        // 记录错误日志
                        log.error("Gateway Error: {}", ex.getMessage(), ex);
                        
                        // 返回错误信息
                        return bufferFactory.wrap(objectMapper.writeValueAsBytes(result));
                    } catch (JsonProcessingException e) {
                        log.error("Error writing response", e);
                        return bufferFactory.wrap(new byte[0]);
                    }
                }));
    }
}