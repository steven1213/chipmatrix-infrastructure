package com.steven.chipmatrix.api.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * <template>desc<template>.
 *
 * @author: steven
 * @date: 2024/11/18 14:09
 */
@SpringBootApplication
@EnableDiscoveryClient 
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
