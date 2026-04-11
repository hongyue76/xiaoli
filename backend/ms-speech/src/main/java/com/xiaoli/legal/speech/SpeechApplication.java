package com.xiaoli.legal.speech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 语音对话服务启动类
 * 提供语音识别(ASR)、语音合成(TTS)、实时语音对话功能
 */
@SpringBootApplication(
    scanBasePackages = {
        "com.xiaoli.legal.speech",        // 当前模块
        "com.xiaoli.legal.common.core",    // 公共核心
        "com.xiaoli.ai.common.core"        // AI 公共模块
    }
)
@EnableDiscoveryClient
@EnableFeignClients
public class SpeechApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpeechApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
