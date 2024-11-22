package com.steven.chipmatrix.api.gateway.filter.global;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class AuthFilter implements GlobalFilter, Ordered {

    private static final String AUTH_TOKEN = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    
    // 白名单路径
    private static final List<String> WHITE_LIST = Arrays.asList(
        "/auth/login",
        "/auth/register",
        "/*/v3/api-docs",
        "/swagger-ui.html",
        "/swagger-resources/**"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        // 白名单放行
        if (isWhitePath(path)) {
            return chain.filter(exchange);
        }

        // 获取token
        String token = request.getHeaders().getFirst(AUTH_TOKEN);
        if (!StringUtils.hasText(token) || !token.startsWith(TOKEN_PREFIX)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 验证token (这里只是示例，实际需要调用认证服务验证token)
        String actualToken = token.substring(TOKEN_PREFIX.length());
        if (!validateToken(actualToken)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 传递用户信息
        ServerHttpRequest newRequest = request.mutate()
                .header("X-User-Id", getUserIdFromToken(actualToken))
                .build();
        
        return chain.filter(exchange.mutate().request(newRequest).build());
    }

    @Override
    public int getOrder() {
        return -100; // 确保在其他过滤器之前执行
    }

    private boolean isWhitePath(String path) {
        return WHITE_LIST.stream().anyMatch(pattern -> 
            path.matches(pattern.replace("/**", ".*"))
        );
    }

    private boolean validateToken(String token) {
        // TODO: 实现token验证逻辑，可以调用认证服务
        return true;
    }

    private String getUserIdFromToken(String token) {
        // TODO: 从token中解析用户ID
        return "test-user-id";
    }
}