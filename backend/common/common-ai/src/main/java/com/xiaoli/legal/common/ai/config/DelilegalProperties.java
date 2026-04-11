package com.xiaoli.legal.common.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 得理法搜API配置
 */
@Configuration
@ConfigurationProperties(prefix = "delilegal")
public class DelilegalProperties {

    /**
     * API基础地址
     */
    private String baseUrl = "https://openapi.delilegal.com";

    /**
     * 应用ID（鉴权用）
     */
    private String appId;

    /**
     * 应用密钥（鉴权用）
     */
    private String secret;

    /**
     * API Key（保留兼容性，优先使用appId和secret）
     */
    private String apiKey;

    /**
     * 请求超时时间(毫秒)
     */
    private Integer timeout = 30000;

    // Getters and Setters
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public Integer getTimeout() { return timeout; }
    public void setTimeout(Integer timeout) { this.timeout = timeout; }
}
