package com.xiaoli.legal.ms.consult.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 小理AI配置
 */
@Component
@ConfigurationProperties(prefix = "xiaoli.api")
public class XiaoliConfig {

    private String key;
    private String baseUrl = "https://api.xiaoli.ai";
    private String model = "xiaoli-legal";
    private Integer timeout = 30000;
    private Integer maxRetries = 3;

    // Getters
    public String getKey() { return key; }
    public String getBaseUrl() { return baseUrl; }
    public String getModel() { return model; }
    public Integer getTimeout() { return timeout; }
    public Integer getMaxRetries() { return maxRetries; }

    // Setters
    public void setKey(String key) { this.key = key; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public void setModel(String model) { this.model = model; }
    public void setTimeout(Integer timeout) { this.timeout = timeout; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }
}
