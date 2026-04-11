package com.xiaoli.legal.decision;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 司法辅助决策系统启动类
 * 提供量刑建议、审判预测、证据评估、判决预测等功能
 */
@SpringBootApplication(
    scanBasePackages = {
        "com.xiaoli.legal.decision",      // 当前模块
        "com.xiaoli.legal.common.core",    // 公共核心
        "com.xiaoli.ai.common.core"        // AI 公共模块
    }
)
@EnableDiscoveryClient
@EnableFeignClients
public class DecisionApplication {

    public static void main(String[] args) {
        SpringApplication.run(DecisionApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
