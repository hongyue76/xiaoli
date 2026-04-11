package com.xiaoli.intent.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 意图路由服务启动类
 */
@SpringBootApplication(
    scanBasePackages = {
        "com.xiaoli.intent.core",         // 当前模块
        "com.xiaoli.ai.common.core",       // AI 公共模块
        "com.xiaoli.legal.common.core"     // 公共核心模块
    }
)
public class IntentRouterApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntentRouterApplication.class, args);
        System.out.println("=================================================");
        System.out.println("意图路由服务启动成功！");
        System.out.println("访问地址: http://localhost:8087/intent-router");
        System.out.println("健康检查: http://localhost:8087/intent-router/health");
        System.out.println("=================================================");
    }
}
