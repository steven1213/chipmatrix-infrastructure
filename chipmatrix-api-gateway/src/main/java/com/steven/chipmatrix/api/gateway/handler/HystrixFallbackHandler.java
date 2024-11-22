package com.steven.chipmatrix.api.gateway.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务降级处理
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HystrixFallbackHandler implements HandlerFunction<ServerResponse> {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        Map<String, Object> result = new HashMap<>(4);
        result.put("code", HttpStatus.SERVICE_UNAVAILABLE.value());
        result.put("message", "服务暂时不可用，请稍后重试");
        result.put("path", request.path());
        result.put("timestamp", System.currentTimeMillis());

        try {
            return ServerResponse
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(objectMapper.writeValueAsString(result)));
        } catch (JsonProcessingException e) {
            log.error("Error writing fallback response", e);
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}