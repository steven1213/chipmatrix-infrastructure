package com.steven.chipmatrix.api.gateway.filter.factory;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class RateLimiterFilterFactory extends AbstractGatewayFilterFactory<RateLimiterFilterFactory.Config> {

    private final RedisRateLimiter redisRateLimiter;
    private final KeyResolver keyResolver;

    public RateLimiterFilterFactory(RedisRateLimiter redisRateLimiter, KeyResolver keyResolver) {
        super(Config.class);
        this.redisRateLimiter = redisRateLimiter;
        this.keyResolver = keyResolver;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            return redisRateLimiter.isAllowed(config.getRouteId(), keyResolver.resolve(exchange))
                    .flatMap(response -> {
                        if (!response.isAllowed()) {
                            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                            return exchange.getResponse().setComplete();
                        }
                        return chain.filter(exchange);
                    });
        };
    }

    public static class Config {
        private String routeId;
        private int replenishRate;
        private int burstCapacity;

        public String getRouteId() {
            return routeId;
        }

        public void setRouteId(String routeId) {
            this.routeId = routeId;
        }

        public int getReplenishRate() {
            return replenishRate;
        }

        public void setReplenishRate(int replenishRate) {
            this.replenishRate = replenishRate;
        }

        public int getBurstCapacity() {
            return burstCapacity;
        }

        public void setBurstCapacity(int burstCapacity) {
            this.burstCapacity = burstCapacity;
        }
    }
}