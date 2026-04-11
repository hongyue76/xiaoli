package com.xiaoli.legal.ms.caseinfo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 案例检索模块启动类
 */
@SpringBootApplication(
    scanBasePackages = {
        "com.xiaoli.legal.ms.caseinfo",    // 当前模块
        "com.xiaoli.legal.common.core",    // 公共核心
        "com.xiaoli.ai.common.core"        // AI 公共模块
    }
)
@MapperScan("com.xiaoli.legal.ms.caseinfo.mapper")
public class CaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaseApplication.class, args);
    }
}
