package com.xiaoli.legal.evidence.service;

import com.xiaoli.legal.evidence.model.dto.EvidenceAnalysisRequest;
import com.xiaoli.legal.evidence.model.dto.EvidenceAnalysisResponse;
import com.xiaoli.legal.evidence.model.entity.Evidence;

import java.util.List;

/**
 * 证据服务接口
 */
public interface EvidenceService {

    /**
     * 分析证据材料
     */
    EvidenceAnalysisResponse analyzeEvidence(EvidenceAnalysisRequest request, Long userId);

    /**
     * 上传并分析证据
     */
    EvidenceAnalysisResponse analyzeByFile(Long fileId, Long userId);

    /**
     * 获取证据详情
     */
    Evidence getEvidenceDetail(Long evidenceId);

    /**
     * 获取证据分析结果
     */
    EvidenceAnalysisResponse getAnalysisResult(Long evidenceId);

    /**
     * 获取证据列表
     */
    List<Evidence> getEvidenceList(Long caseId, Long userId);

    /**
     * 删除证据
     */
    void deleteEvidence(Long evidenceId);

    /**
     * 获取证据类型列表
     */
    List<String> getEvidenceTypes();
}
