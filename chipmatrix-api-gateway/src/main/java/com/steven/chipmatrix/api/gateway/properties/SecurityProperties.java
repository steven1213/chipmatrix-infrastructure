package com.steven.chipmatrix.api.gateway.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <template>desc<template>.
 *
 * @author: steven
 * @date: 2024/11/18 14:09
 */
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    private String tokenHeader;
    private String tokenSecret;
    private Long tokenExpiration;



    
}
