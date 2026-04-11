package com.xiaoli.legal.ms.contract.service;

import com.xiaoli.legal.common.core.domain.PageResult;
import com.xiaoli.legal.ms.contract.model.dto.ContractReviewRequest;
import com.xiaoli.legal.ms.contract.model.dto.ContractReviewResponse;
import com.xiaoli.legal.ms.contract.model.entity.Contract;

import java.util.List;

/**
 * 合同服务接口
 */
public interface ContractService {

    /**
     * 审查合同
     */
    ContractReviewResponse reviewContract(ContractReviewRequest request, Long userId);

    /**
     * 上传合同文件并审查
     */
    ContractReviewResponse reviewByFile(Long fileId, Long userId);

    /**
     * 获取合同详情
     */
    Contract getContractDetail(Long contractId);

    /**
     * 获取审查结果
     */
    ContractReviewResponse getReviewResult(Long contractId);

    /**
     * 获取合同列表
     */
    PageResult<Contract> getContractList(Long userId, String status, Long current, Long size);

    /**
     * 删除合同
     */
    void deleteContract(Long contractId);

    /**
     * 获取合同类型列表
     */
    List<String> getContractTypes();
}
