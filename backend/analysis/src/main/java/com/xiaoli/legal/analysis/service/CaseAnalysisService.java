package com.xiaoli.legal.analysis.service;

import com.xiaoli.legal.analysis.model.dto.CaseAnalysisRequest;
import com.xiaoli.legal.analysis.model.dto.CaseAnalysisResponse;
import com.xiaoli.legal.analysis.model.entity.CaseAnalysis;

import java.util.List;

/**
 * 案件分析服务接口
 */
public interface CaseAnalysisService {

    /**
     * 分析案件
     */
    CaseAnalysisResponse analyzeCase(CaseAnalysisRequest request, Long userId);

    /**
     * 获取分析结果
     */
    CaseAnalysisResponse getAnalysisResult(Long analysisId);

    /**
     * 获取分析历史
     */
    List<CaseAnalysis> getAnalysisHistory(Long userId, Long caseId);

    /**
     * 删除分析记录
     */
    void deleteAnalysis(Long analysisId);
}
