package com.xiaoli.legal.common.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 腾讯元器配置
 */
@Configuration
@ConfigurationProperties(prefix = "tencent.yuanqi")
public class YuanqiProperties {

    /**
     * API请求地址
     */
    private String baseUrl = "https://api.yuanqi.tencent.com";

    /**
     * 智能体ID
     */
    private String assistantId;

    /**
     * API Token
     */
    private String token;

    /**
     * 请求超时时间(毫秒)
     */
    private Integer timeout = 60000;

    // Getters and Setters
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getAssistantId() { return assistantId; }
    public void setAssistantId(String assistantId) { this.assistantId = assistantId; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Integer getTimeout() { return timeout; }
    public void setTimeout(Integer timeout) { this.timeout = timeout; }
}
