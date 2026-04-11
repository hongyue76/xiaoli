package com.xiaoli.legal.ms.document;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 法律文书模块启动类
 */
@SpringBootApplication(
    scanBasePackages = {
        "com.xiaoli.legal.ms.document",    // 当前模块
        "com.xiaoli.legal.common.core",    // 公共核心
        "com.xiaoli.ai.common.core"        // AI 公共模块
    }
)
@MapperScan("com.xiaoli.legal.ms.document.mapper")
@EnableDiscoveryClient
@EnableFeignClients
public class DocumentApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentApplication.class, args);
    }
}
