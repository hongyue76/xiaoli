package com.xiaoli.legal.decision.service;

import com.xiaoli.legal.decision.model.dto.DecisionRequest;
import com.xiaoli.legal.decision.model.dto.DecisionResponse;

/**
 * 司法决策服务接口
 */
public interface DecisionService {
    
    /**
     * 生成司法决策建议
     * @param request 决策请求
     * @return 决策响应
     */
    DecisionResponse generateDecision(DecisionRequest request);
    
    /**
     * 获取量刑建议
     * @param request 决策请求
     * @return 量刑建议
     */
    DecisionResponse.SentencingInfo getSentencingSuggestion(DecisionRequest request);
    
    /**
     * 获取审判预测
     * @param request 决策请求
     * @return 审判预测
     */
    DecisionResponse.TrialPredictionInfo getTrialPrediction(DecisionRequest request);
    
    /**
     * 查找相似判例
     * @param caseType 案件类型
     * @param caseDescription 案件描述
     * @return 相似案例列表
     */
    DecisionResponse.ReferenceCaseInfo findSimilarJudgments(String caseType, String caseDescription);
    
    /**
     * 评估案件风险
     * @param request 决策请求
     * @return 风险评估
     */
    DecisionResponse.RiskAssessment assessRisk(DecisionRequest request);
}
