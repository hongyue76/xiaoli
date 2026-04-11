package com.xiaoli.legal.common.ai;

import com.xiaoli.legal.common.ai.config.XiaoliAiProperties;
import com.xiaoli.legal.common.ai.service.*;
import com.xiaoli.legal.common.ai.service.impl.*;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 小理AI自动配置
 */
@Configuration
@EnableConfigurationProperties(XiaoliAiProperties.class)
@ConditionalOnProperty(prefix = "xiaoli.ai", name = "enabled", havingValue = "true", matchIfMissing = true)
public class XiaoliAiAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public OkHttpClient okHttpClient(XiaoliAiProperties properties) {
        // 创建HTTP客户端
        return new OkHttpClient.Builder()
                .connectTimeout(properties.getTimeout(), java.util.concurrent.TimeUnit.MILLISECONDS)
                .readTimeout(properties.getTimeout(), java.util.concurrent.TimeUnit.MILLISECONDS)
                .writeTimeout(properties.getTimeout(), java.util.concurrent.TimeUnit.MILLISECONDS)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public XiaoliChatService xiaoliChatService(OkHttpClient okHttpClient, XiaoliAiProperties properties) {
        return new XiaoliChatServiceImpl(okHttpClient, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public EmbeddingService embeddingService(XiaoliAiProperties properties, OkHttpClient okHttpClient) {
        return new EmbeddingServiceImpl(properties, okHttpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public TtsService ttsService(XiaoliAiProperties properties, OkHttpClient okHttpClient) {
        return new TtsServiceImpl(properties, okHttpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public AsrService asrService(XiaoliAiProperties properties, OkHttpClient okHttpClient) {
        return new AsrServiceImpl(properties, okHttpClient);
    }
}
