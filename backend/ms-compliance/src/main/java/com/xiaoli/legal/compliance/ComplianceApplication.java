package com.xiaoli.legal.compliance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 企业合规管理系统启动类
 * 提供合规审查、风险评估、合规培训、整改跟踪等功能
 */
@SpringBootApplication(
    scanBasePackages = {
        "com.xiaoli.legal.compliance",     // 当前模块
        "com.xiaoli.legal.common.core",    // 公共核心
        "com.xiaoli.ai.common.core"        // AI 公共模块
    }
)
@EnableDiscoveryClient
@EnableFeignClients
public class ComplianceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComplianceApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
