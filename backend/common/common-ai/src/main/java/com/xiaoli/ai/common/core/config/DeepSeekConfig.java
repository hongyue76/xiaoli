package com.xiaoli.ai.common.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * DeepSeek API 配置
 */
@Configuration
public class DeepSeekConfig {

    private static final Logger log = LoggerFactory.getLogger(DeepSeekConfig.class);

    @Value("${deepseek.api.url:https://api.deepseek.com/v1}")
    private String apiUrl;

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.model:deepseek-chat}")
    private String model;

    @Value("${deepseek.api.timeout:60}")
    private int timeout;

    @Bean
    public OkHttpClient deepSeekOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .addInterceptor(new DeepSeekAuthInterceptor(apiKey))
                .addInterceptor(new DeepSeekLoggingInterceptor())
                .build();
    }

    /**
     * DeepSeek API 认证拦截器
     */
    public static class DeepSeekAuthInterceptor implements Interceptor {
        private final String apiKey;

        public DeepSeekAuthInterceptor(String apiKey) {
            this.apiKey = apiKey;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request requestWithAuth = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .build();
            return chain.proceed(requestWithAuth);
        }
    }

    /**
     * DeepSeek API 日志拦截器
     */
    public static class DeepSeekLoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            long startTime = System.currentTimeMillis();
            
            log.info("DeepSeek API Request: {} {}", request.method(), request.url());
            
            Response response = chain.proceed(request);
            long duration = System.currentTimeMillis() - startTime;
            
            log.info("DeepSeek API Response: {} {} ({}ms)", 
                    response.code(), request.url(), duration);
            
            return response;
        }
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getModel() {
        return model;
    }
}
