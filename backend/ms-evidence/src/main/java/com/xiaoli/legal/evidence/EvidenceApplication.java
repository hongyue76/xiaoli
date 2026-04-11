package com.xiaoli.legal.evidence;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 证据材料分析模块启动类
 */
@SpringBootApplication(
    scanBasePackages = {
        "com.xiaoli.legal.evidence",       // 当前模块
        "com.xiaoli.legal.common.core",    // 公共核心
        "com.xiaoli.ai.common.core"        // AI 公共模块
    }
)
@MapperScan("com.xiaoli.legal.evidence.mapper")
@EnableDiscoveryClient
@EnableFeignClients
public class EvidenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvidenceApplication.class, args);
    }
}
