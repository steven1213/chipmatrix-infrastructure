package com.steven.chipmatrix.api.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "gateway.auth")
public class AuthProperties {
    private List<String> whiteList;
    
    public List<String> getWhiteList() {
        return whiteList;
    }
    
    public void setWhiteList(List<String> whiteList) {
        this.whiteList = whiteList;
    }
}