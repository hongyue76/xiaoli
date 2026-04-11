package com.xiaoli.legal.ms.contract.controller;

import com.xiaoli.legal.common.core.domain.PageResult;
import com.xiaoli.legal.common.core.domain.Result;
import com.xiaoli.legal.ms.contract.model.dto.ContractReviewRequest;
import com.xiaoli.legal.ms.contract.model.dto.ContractReviewResponse;
import com.xiaoli.legal.ms.contract.model.entity.Contract;
import com.xiaoli.legal.ms.contract.service.ContractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 合同审查接口
 */
@RestController
@RequestMapping("/api/contract")
public class ContractController {

    private static final Logger log = LoggerFactory.getLogger(ContractController.class);

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    /**
     * 审查合同
     */
    @PostMapping("/review")
    public Result<ContractReviewResponse> reviewContract(
            @RequestBody @Validated ContractReviewRequest request,
            @RequestParam(defaultValue = "1") Long userId) {
        log.info("收到合同审查请求: {}", request.getName());
        ContractReviewResponse result = contractService.reviewContract(request, userId);
        return Result.success(result);
    }

    /**
     * 获取合同详情
     */
    @GetMapping("/{id}")
    public Result<Contract> getContractDetail(@PathVariable Long id) {
        Contract result = contractService.getContractDetail(id);
        return Result.success(result);
    }

    /**
     * 获取审查结果
     */
    @GetMapping("/{id}/review-result")
    public Result<ContractReviewResponse> getReviewResult(@PathVariable Long id) {
        ContractReviewResponse result = contractService.getReviewResult(id);
        return Result.success(result);
    }

    /**
     * 获取合同列表
     */
    @GetMapping("/list")
    public Result<PageResult<Contract>> getContractList(
            @RequestParam Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        PageResult<Contract> result = contractService.getContractList(userId, status, current, size);
        return Result.success(result);
    }

    /**
     * 删除合同
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteContract(@PathVariable Long id) {
        contractService.deleteContract(id);
        return Result.success();
    }

    /**
     * 获取合同类型
     */
    @GetMapping("/types")
    public Result<List<String>> getContractTypes() {
        List<String> types = contractService.getContractTypes();
        return Result.success(types);
    }
}
