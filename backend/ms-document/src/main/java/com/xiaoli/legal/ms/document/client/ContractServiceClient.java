package com.xiaoli.legal.ms.document.client;

import com.xiaoli.legal.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 合同审查服务 Feign Client
 */
@FeignClient(name = "ms-contract")
public interface ContractServiceClient {

    /**
     * 审查合同
     */
    @PostMapping("/api/contract/review")
    Result<Map<String, Object>> reviewContract(@RequestBody Map<String, Object> contractData);

    /**
     * 获取合同模板
     */
    @GetMapping("/api/contract/template")
    Result<List<Map<String, Object>>> getContractTemplates(@RequestParam("type") String type);
}
