package com.xiaoli.legal.common.ai.config;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

/**
 * 小理AI HTTP客户端配置
 */
@Configuration
public class XiaoliAiHttpConfig {

    private final XiaoliAiProperties properties;

    public XiaoliAiHttpConfig(XiaoliAiProperties properties) {
        this.properties = properties;
    }

    @Bean
    @Primary
    public OkHttpClient okHttpClient() {
        // 日志拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // 认证拦截器
        Interceptor authInterceptor = chain -> {
            Request original = chain.request();
            Request.Builder builder = original.newBuilder()
                    .header("Authorization", "Bearer " + properties.getApiKey())
                    .header("Content-Type", "application/json")
                    .method(original.method(), original.body());
            return chain.proceed(builder.build());
        };

        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .connectTimeout(properties.getTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(properties.getTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(properties.getTimeout(), TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }
}
