package com.steven.chipmatrix.api.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <template>desc<template>.
 *
 * @author: steven
 * @date: 2024/11/18 14:08
 */
@Component
@ConfigurationProperties(prefix = "route")
public class RouteProperties {

    private List<Route> routes;

    @Data
    public static class Route {
        private String id;
        private String uri;
    }

}
