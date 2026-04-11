package com.xiaoli.legal.analysis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 案件分析模块启动类
 */
@SpringBootApplication(
    scanBasePackages = {
        "com.xiaoli.legal.analysis",       // 当前模块
        "com.xiaoli.legal.common.core",    // 公共核心
        "com.xiaoli.ai.common.core"        // AI 公共模块
    }
)
@MapperScan("com.xiaoli.legal.analysis.mapper")
@EnableDiscoveryClient
@EnableFeignClients
public class AnalysisApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnalysisApplication.class, args);
    }
}
