package com.xiaoli.legal.common.core.config;

import feign.Logger;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * OpenFeign 全局配置
 * 配置超时、重试、日志等
 */
@Configuration
public class FeignConfig {

    /**
     * Feign 日志级别
     * NONE: 无日志
     * BASIC: 仅请求方法、URL、响应状态码
     * HEADERS: 请求头和响应头
     * FULL: 完整请求和响应
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    /**
     * 请求超时配置
     * 使用默认的重试间隔
     */
    @Bean
    Retryer feignRetryer() {
        return new Retryer.Default();
    }

    /**
     * 使用 Spring MVC 的消息转换器
     */
    @Bean
    Encoder feignEncoder() {
        ObjectFactory<HttpMessageConverters> messageConverters = HttpMessageConverters::new;
        return new SpringEncoder(messageConverters);
    }

    @Bean
    Decoder feignDecoder() {
        ObjectFactory<HttpMessageConverters> messageConverters = HttpMessageConverters::new;
        return new SpringDecoder(messageConverters);
    }
}
