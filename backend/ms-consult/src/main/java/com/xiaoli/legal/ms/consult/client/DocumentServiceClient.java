package com.xiaoli.legal.ms.consult.client;

import com.xiaoli.legal.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 文书生成服务 Feign Client
 * 通过 Nacos 服务发现调用文书服务
 */
@FeignClient(name = "ms-document")
public interface DocumentServiceClient {

    /**
     * 获取文档模板
     */
    @GetMapping("/api/document/template/{templateId}")
    Result<Map<String, Object>> getTemplate(@PathVariable("templateId") String templateId);

    /**
     * 获取用户文档列表
     */
    @GetMapping("/api/document/list")
    Result<Map<String, Object>> getUserDocuments();
}
