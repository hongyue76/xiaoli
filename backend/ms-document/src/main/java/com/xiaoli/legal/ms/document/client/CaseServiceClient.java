package com.xiaoli.legal.ms.document.client;

import com.xiaoli.legal.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 案例检索服务 Feign Client
 */
@FeignClient(name = "ms-caseinfo")
public interface CaseServiceClient {

    /**
     * 获取案例详情
     */
    @GetMapping("/api/case/detail")
    Result<Map<String, Object>> getCaseDetail(@RequestParam("caseId") String caseId);
}
