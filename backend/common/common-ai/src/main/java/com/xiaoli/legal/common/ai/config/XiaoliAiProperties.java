package com.xiaoli.legal.common.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * AI配置 - 接收腾讯元宝体贴体
 */
@Configuration
@ConfigurationProperties(prefix = "xiaoli.ai")
@Primary
public class XiaoliAiProperties {

    /**
     * API基础地址
     */
    private String baseUrl = "https://api.yuanqi.tencent.com";

    /**
     * API Key
     */
    private String apiKey;

    /**
     * 模型名称
     */
    private String model = "hunyuan-pro";

    /**
     * Embedding模型
     */
    private String embeddingModel = "text-embedding-001";

    /**
     * TTS模型
     */
    private String ttsModel = "tts-001";

    /**
     * ASR模型
     */
    private String asrModel = "asr-001";

    /**
     * 请求超时(毫秒)
     */
    private Integer timeout = 60000;

    /**
     * 最大重试次数
     */
    private Integer maxRetries = 3;

    // Getters and Setters
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getEmbeddingModel() { return embeddingModel; }
    public void setEmbeddingModel(String embeddingModel) { this.embeddingModel = embeddingModel; }

    public String getTtsModel() { return ttsModel; }
    public void setTtsModel(String ttsModel) { this.ttsModel = ttsModel; }

    public String getAsrModel() { return asrModel; }
    public void setAsrModel(String asrModel) { this.asrModel = asrModel; }

    public Integer getTimeout() { return timeout; }
    public void setTimeout(Integer timeout) { this.timeout = timeout; }

    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }
}
