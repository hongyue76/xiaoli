package com.xiaoli.legal.ms.consult.client;

import com.xiaoli.legal.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 案例检索服务 Feign Client
 * 通过 Nacos 服务发现调用案例服务
 */
@FeignClient(name = "ms-caseinfo")
public interface CaseServiceClient {

    /**
     * 搜索案例
     */
    @GetMapping("/api/case/search")
    Result<List<Map<String, Object>>> searchCases(@RequestParam("query") String query,
                                                    @RequestParam(value = "limit", defaultValue = "5") Integer limit);

    /**
     * 获取案例详情
     */
    @GetMapping("/api/case/detail")
    Result<Map<String, Object>> getCaseDetail(@RequestParam("caseId") String caseId);

    /**
     * 相关案例推荐
     */
    @GetMapping("/api/case/related")
    Result<List<Map<String, Object>>> getRelatedCases(@RequestParam("caseId") String caseId);
}
