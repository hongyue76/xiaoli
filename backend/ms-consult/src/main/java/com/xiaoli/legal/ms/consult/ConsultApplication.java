package com.xiaoli.legal.ms.consult;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 法律咨询模块启动类
 */
@SpringBootApplication(
    scanBasePackages = {
        "com.xiaoli.legal.ms.consult",     // 当前模块
        "com.xiaoli.legal.common",        // 公共模块
        "com.xiaoli.ai.common.core"        // AI 公共模块
    }
)
@MapperScan("com.xiaoli.legal.ms.consult.mapper")
@EnableDiscoveryClient
@EnableFeignClients
public class ConsultApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsultApplication.class, args);
    }
}
