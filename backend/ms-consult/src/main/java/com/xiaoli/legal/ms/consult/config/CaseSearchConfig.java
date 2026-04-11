package com.xiaoli.legal.ms.consult.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 案例检索服务配置
 */
@Configuration
@ConfigurationProperties(prefix = "case.search")
public class CaseSearchConfig {

    private boolean enabled = true;
    private String baseUrl = "http://localhost:8083";
    private int timeout = 10000;
    private int defaultLimit = 3;

    // Getters
    public boolean isEnabled() { return enabled; }
    public String getBaseUrl() { return baseUrl; }
    public int getTimeout() { return timeout; }
    public int getDefaultLimit() { return defaultLimit; }

    // Setters
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public void setTimeout(int timeout) { this.timeout = timeout; }
    public void setDefaultLimit(int defaultLimit) { this.defaultLimit = defaultLimit; }
}
