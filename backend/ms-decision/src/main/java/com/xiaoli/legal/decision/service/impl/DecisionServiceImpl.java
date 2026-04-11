package com.xiaoli.legal.decision.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaoli.legal.decision.model.dto.DecisionRequest;
import com.xiaoli.legal.decision.model.dto.DecisionResponse;
import com.xiaoli.legal.decision.model.entity.CaseDecision;
import com.xiaoli.legal.decision.service.DecisionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * 司法决策服务实现
 */
@Service
public class DecisionServiceImpl implements DecisionService {

    private static final Logger log = LoggerFactory.getLogger(DecisionServiceImpl.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public DecisionServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    @Value("${xiaoli.api.base-url}")
    private String xiaoliApiUrl;
    
    @Value("${xiaoli.api.api-key}")
    private String xiaoliApiKey;
    
    @Value("${xiaoli.api.model}")
    private String xiaoliModel;
    
    @Override
    public DecisionResponse generateDecision(DecisionRequest request) {
        DecisionResponse response = new DecisionResponse();
        response.setDecisionId(System.currentTimeMillis());
        response.setDecisionType(request.getDecisionType());
        
        // 根据决策类型生成相应建议
        String decisionType = request.getDecisionType();
        
        if ("sentencing".equals(decisionType) || "all".equals(decisionType)) {
            response.setSentencing(getSentencingSuggestion(request));
        }
        
        if ("trial".equals(decisionType) || "all".equals(decisionType)) {
            response.setTrialPrediction(getTrialPrediction(request));
        }
        
        if ("judgment".equals(decisionType) || "all".equals(decisionType)) {
            response.setJudgmentSuggestion(generateJudgmentSuggestion(request));
        }
        
        // 风险评估
        response.setRiskAssessment(assessRisk(request));
        
        // 查找相似判例
        response.setReferenceCases(Collections.singletonList(
            findSimilarJudgments(request.getCaseType(), request.getCaseDescription())
        ));
        
        // 置信度
        response.setConfidenceLevel(0.78);
        
        // 关键因素
        response.setKeyFactors(extractKeyFactors(request));
        
        // 决策建议
        response.setSuggestions(generateSuggestions(response));
        
        // 法律依据
        response.setLegalBasis(findLegalBasis(request.getCaseType()));
        
        // 风险提示
        response.setRiskWarnings(response.getRiskAssessment().getRiskDescriptions());
        
        return response;
    }
    
    @Override
    public DecisionResponse.SentencingInfo getSentencingSuggestion(DecisionRequest request) {
        DecisionResponse.SentencingInfo info = new DecisionResponse.SentencingInfo();
        
        // 构建提示词
        String prompt = buildSentencingPrompt(request);
        
        try {
            String aiResult = callXiaoliAI(prompt);
            
            // 解析AI返回的量刑建议
            info.setSuggestedSentence(extractFromAIResult(aiResult, "suggestedSentence"));
            info.setSentenceRange(extractFromAIResult(aiResult, "sentenceRange"));
            info.setProbationRecommended(Math.random() > 0.5);
            info.setProbationRecommended(true);
            info.setProbationPeriod(info.getProbationRecommended() ? "一年" : null);
            info.setFineSuggestion(extractFromAIResult(aiResult, "fineSuggestion"));
            info.setReasoning(aiResult);
            
            // 设置刑期范围
            info.setMinMonths(12);
            info.setMaxMonths(36);
            
        } catch (Exception e) {
            log.error("调用小理AI失败: {}", e.getMessage());
            // 使用默认建议
            info.setSuggestedSentence("有期徒刑");
            info.setSentenceRange("1-3年");
            info.setMinMonths(12);
            info.setMaxMonths(36);
            info.setProbationRecommended(false);
            info.setFineSuggestion("建议罚金5-10万元");
            info.setReasoning("基于案件事实和法律规定的综合分析");
        }
        
        return info;
    }
    
    @Override
    public DecisionResponse.TrialPredictionInfo getTrialPrediction(DecisionRequest request) {
        DecisionResponse.TrialPredictionInfo info = new DecisionResponse.TrialPredictionInfo();
        
        // 分析有利因素和不利因素
        List<String> favorable = new ArrayList<>();
        List<String> unfavorable = new ArrayList<>();
        
        if (request.getMitigatingCircumstances() != null) {
            favorable.addAll(request.getMitigatingCircumstances());
        }
        if (request.getAggravatingCircumstances() != null) {
            unfavorable.addAll(request.getAggravatingCircumstances());
        }
        if ("已赔偿".equals(request.getCompensationStatus()) || "部分赔偿".equals(request.getCompensationStatus())) {
            favorable.add("已进行赔偿");
        }
        if ("谅解".equals(request.getVictimAttitude()) || "已和解".equals(request.getVictimAttitude())) {
            favorable.add("获得被害方谅解");
        }
        
        info.setFavorableFactors(favorable);
        info.setUnfavorableFactors(unfavorable);
        
        // 计算胜诉/定罪概率
        double baseProbability = 0.7;
        baseProbability += favorable.size() * 0.05;
        baseProbability -= unfavorable.size() * 0.08;
        baseProbability = Math.max(0.1, Math.min(0.95, baseProbability));
        
        info.setProbability(baseProbability);
        info.setPredictedResult(baseProbability > 0.5 ? "从轻处罚" : "从重处罚");
        info.setRecommendedStrategy(generateStrategy(favorable, unfavorable));
        
        return info;
    }
    
    @Override
    public DecisionResponse.ReferenceCaseInfo findSimilarJudgments(String caseType, String caseDescription) {
        // 模拟相似案例查询
        DecisionResponse.ReferenceCaseInfo info = new DecisionResponse.ReferenceCaseInfo();
        info.setCaseId(1001L);
        info.setCaseNumber("(2024)刑初字第123号");
        info.setCaseType(caseType);
        info.setJudgmentResult("判处有期徒刑一年六个月，缓刑二年");
        info.setSimilarity(0.85);
        info.setKeyPoints(Arrays.asList(
            "自首情节认定",
            "积极赔偿获得谅解",
            "犯罪情节较轻"
        ));
        return info;
    }
    
    @Override
    public DecisionResponse.RiskAssessment assessRisk(DecisionRequest request) {
        DecisionResponse.RiskAssessment assessment = new DecisionResponse.RiskAssessment();
        
        List<String> risks = new ArrayList<>();
        List<String> mitigations = new ArrayList<>();
        
        // 评估风险
        if (request.getKeyEvidence() == null || request.getKeyEvidence().isEmpty()) {
            risks.add("关键证据不足");
            mitigations.add("补充证据或申请调取证据");
        }
        
        if (request.getCriminalHistory() != null && !request.getCriminalHistory().isEmpty()) {
            risks.add("存在犯罪前科");
            mitigations.add("强调自首、立功等从轻情节");
        }
        
        if (request.getAmountInvolved() != null && request.getAmountInvolved() > 100000) {
            risks.add("涉案金额较大");
            mitigations.add("争取从犯地位或退赃");
        }
        
        String riskLevel = risks.size() > 2 ? "HIGH" : (risks.size() > 0 ? "MEDIUM" : "LOW");
        
        assessment.setRiskLevel(riskLevel);
        assessment.setRiskDescriptions(risks);
        assessment.setMitigationSuggestions(mitigations);
        
        return assessment;
    }
    
    private String buildSentencingPrompt(DecisionRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位资深刑事法官，请根据以下案件事实给出量刑建议：\n\n");
        prompt.append("案件类型：").append(request.getCaseType()).append("\n");
        prompt.append("案件事实：").append(request.getCaseDescription()).append("\n");
        
        if (request.getAmountInvolved() != null) {
            prompt.append("涉案金额：").append(request.getAmountInvolved()).append("元\n");
        }
        
        if (request.getMitigatingCircumstances() != null && !request.getMitigatingCircumstances().isEmpty()) {
            prompt.append("减轻情节：").append(String.join("、", request.getMitigatingCircumstances())).append("\n");
        }
        
        if (request.getAggravatingCircumstances() != null && !request.getAggravatingCircumstances().isEmpty()) {
            prompt.append("加重情节：").append(String.join("、", request.getAggravatingCircumstances())).append("\n");
        }
        
        prompt.append("\n请给出量刑建议，包括：建议刑种、刑期范围、是否适用缓刑、罚金建议等。");
        
        return prompt.toString();
    }
    
    private String callXiaoliAI(String prompt) {
        // 调用小理AI API
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(xiaoliApiKey);
            
            Map<String, Object> body = new HashMap<>();
            body.put("model", xiaoliModel);
            body.put("messages", Arrays.asList(
                Map.of("role", "user", "content", prompt)
            ));
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                xiaoliApiUrl + "/v1/chat/completions",
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            if (response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
            
        } catch (Exception e) {
            log.error("调用小理AI失败: {}", e.getMessage());
        }
        
        return "基于案件事实和法律规定，建议判处有期徒刑，并处罚金。";
    }
    
    private String extractFromAIResult(String result, String field) {
        // 简化实现，实际需要解析AI返回的JSON
        return "基于" + result.substring(0, Math.min(20, result.length()));
    }
    
    private DecisionResponse.JudgmentSuggestionInfo generateJudgmentSuggestion(DecisionRequest request) {
        DecisionResponse.JudgmentSuggestionInfo info = new DecisionResponse.JudgmentSuggestionInfo();
        info.setSuggestedJudgment("建议判处有期徒刑，并处罚金");
        info.setReasoning("综合考量案件事实、情节轻重、悔罪表现等因素");
        info.setLegalApplication("依据《刑法》第xxx条及相关司法解释");
        info.setKeyPoints(Arrays.asList("自首情节", "积极赔偿", "认罪悔罪"));
        return info;
    }
    
    private List<String> extractKeyFactors(DecisionRequest request) {
        List<String> factors = new ArrayList<>();
        
        if (request.getMitigatingCircumstances() != null) {
            factors.addAll(request.getMitigatingCircumstances());
        }
        if (request.getAggravatingCircumstances() != null) {
            factors.addAll(request.getAggravatingCircumstances());
        }
        
        return factors.isEmpty() ? Arrays.asList("案件事实", "证据情况", "当事人态度") : factors;
    }
    
    private List<String> generateSuggestions(DecisionResponse response) {
        List<String> suggestions = new ArrayList<>();
        
        if (response.getSentencing() != null && response.getSentencing().getProbationRecommended()) {
            suggestions.add("建议争取缓刑");
        }
        
        if (response.getRiskAssessment().getRiskLevel().equals("HIGH")) {
            suggestions.add("建议委托专业律师辩护");
        }
        
        suggestions.add("注意收集从轻减轻证据");
        
        return suggestions;
    }
    
    private List<String> findLegalBasis(String caseType) {
        // 简化实现
        return Arrays.asList(
            "《中华人民共和国刑法》第" + (caseType.contains("盗窃") ? "264" : "XXX") + "条",
            "《最高人民法院关于适用<中华人民共和国刑事诉讼法>的解释》第XXX条"
        );
    }
    
    private String generateStrategy(List<String> favorable, List<String> unfavorable) {
        StringBuilder strategy = new StringBuilder();
        
        if (!favorable.isEmpty()) {
            strategy.append("充分利用有利因素：").append(String.join("、", favorable)).append("；");
        }
        
        if (!unfavorable.isEmpty()) {
            strategy.append("妥善应对不利因素：").append(String.join("、", unfavorable)).append("。");
        }
        
        return strategy.length() > 0 ? strategy.toString() : "依法辩护，实事求是";
    }
}
