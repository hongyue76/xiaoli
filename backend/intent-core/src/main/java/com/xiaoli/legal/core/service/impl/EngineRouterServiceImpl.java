package com.xiaoli.legal.core.service.impl;

import com.xiaoli.legal.core.AIEngineType;
import com.xiaoli.legal.core.IntentType;
import com.xiaoli.legal.core.model.EngineSelectionResult;
import com.xiaoli.legal.core.model.IntentResult;
import com.xiaoli.legal.core.model.UserQuery;
import com.xiaoli.legal.core.service.EngineRouterService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 双引擎路由服务实现
 */
@Service
public class EngineRouterServiceImpl implements EngineRouterService {

    /**
     * 意图到默认引擎的映射
     */
    private static final Map<IntentType, AIEngineType> INTENT_ENGINE_MAPPING = new HashMap<>();

    /**
     * 意图的复杂度阈值（高于此值使用LLM）
     */
    private static final Map<IntentType, Double> INTENT_COMPLEXITY_THRESHOLD = new HashMap<>();

    static {
        // 案例检索、文书生成 - 规则引擎
        INTENT_ENGINE_MAPPING.put(IntentType.CASE_SEARCH, AIEngineType.RULE_BASED);
        INTENT_ENGINE_MAPPING.put(IntentType.DOCUMENT, AIEngineType.RULE_BASED);

        // 法律咨询、合同审查、案件分析、证据分析 - LLM引擎
        INTENT_ENGINE_MAPPING.put(IntentType.CONSULT, AIEngineType.LLM_BASED);
        INTENT_ENGINE_MAPPING.put(IntentType.CONTRACT_REVIEW, AIEngineType.LLM_BASED);
        INTENT_ENGINE_MAPPING.put(IntentType.CASE_ANALYSIS, AIEngineType.LLM_BASED);
        INTENT_ENGINE_MAPPING.put(IntentType.EVIDENCE_ANALYSIS, AIEngineType.LLM_BASED);

        // 司法决策、合规检查 - LLM引擎
        INTENT_ENGINE_MAPPING.put(IntentType.DECISION, AIEngineType.LLM_BASED);
        INTENT_ENGINE_MAPPING.put(IntentType.COMPLIANCE, AIEngineType.LLM_BASED);

        // 设置复杂度阈值
        INTENT_COMPLEXITY_THRESHOLD.put(IntentType.CONSULT, 0.4);
        INTENT_COMPLEXITY_THRESHOLD.put(IntentType.CASE_SEARCH, 0.6);
        INTENT_COMPLEXITY_THRESHOLD.put(IntentType.DOCUMENT, 0.5);
        INTENT_COMPLEXITY_THRESHOLD.put(IntentType.CONTRACT_REVIEW, 0.3);
        INTENT_COMPLEXITY_THRESHOLD.put(IntentType.CASE_ANALYSIS, 0.4);
        INTENT_COMPLEXITY_THRESHOLD.put(IntentType.EVIDENCE_ANALYSIS, 0.4);
        INTENT_COMPLEXITY_THRESHOLD.put(IntentType.DECISION, 0.3);
        INTENT_COMPLEXITY_THRESHOLD.put(IntentType.COMPLIANCE, 0.4);
    }

    @Override
    public EngineSelectionResult route(UserQuery query, IntentResult intentResult) {
        IntentType intent = intentResult.getIntent();

        // 评估复杂度
        double complexity = assessComplexity(query, intentResult);

        // 计算阈值
        double threshold = calculateThreshold(intent, complexity);

        // 决定使用哪个引擎
        AIEngineType engineType;
        String reason;

        if (intent == IntentType.UNKNOWN) {
            // 未知意图，使用LLM
            engineType = AIEngineType.LLM_BASED;
            reason = "意图未知，使用LLM引擎进行通用问答";
        } else if (complexity >= threshold) {
            // 高复杂度，使用LLM
            engineType = AIEngineType.LLM_BASED;
            reason = String.format("复杂度%.2f >= 阈值%.2f，使用LLM引擎", complexity, threshold);
        } else if (intentResult.getConfidence() < 0.7) {
            // 低置信度，使用LLM
            engineType = AIEngineType.LLM_BASED;
            reason = String.format("意图置信度%.2f < 0.7，使用LLM引擎提高准确率", intentResult.getConfidence());
        } else {
            // 使用默认引擎
            engineType = INTENT_ENGINE_MAPPING.getOrDefault(intent, AIEngineType.LLM_BASED);
            reason = String.format("根据意图类型%s，使用默认%s引擎",
                intent.getDisplayName(), engineType.getDisplayName());
        }

        // 计算响应时间和成本
        long expectedResponseTime = engineType == AIEngineType.RULE_BASED ? 500L : 2000L;
        String estimatedCost = engineType == AIEngineType.RULE_BASED ? "低" : "中等";

        // 判断是否使用混合模式
        boolean hybridMode = complexity > 0.3 && complexity < 0.7 && engineType == AIEngineType.LLM_BASED;

        return EngineSelectionResult.builder()
            .engineType(engineType)
            .reason(reason)
            .expectedResponseTime(expectedResponseTime)
            .estimatedCost(estimatedCost)
            .hybridMode(hybridMode)
            .ruleEngineRatio(hybridMode ? 0.3 : null)
            .build();
    }

    @Override
    public AIEngineType getEngineByRule(IntentType intent, Double confidence) {
        // 低置信度使用LLM
        if (confidence < 0.7) {
            return AIEngineType.LLM_BASED;
        }

        // 高置信度根据意图类型选择
        return INTENT_ENGINE_MAPPING.getOrDefault(intent, AIEngineType.LLM_BASED);
    }

    @Override
    public Double assessComplexity(UserQuery query, IntentResult intentResult) {
        String queryText = query.getQueryText();
        double complexity = 0.0;

        // 基于文本长度
        int textLength = queryText.length();
        if (textLength < 50) {
            complexity += 0.1;
        } else if (textLength < 150) {
            complexity += 0.3;
        } else if (textLength < 300) {
            complexity += 0.6;
        } else {
            complexity += 0.8;
        }

        // 基于问题类型
        if (queryText.contains("?") || queryText.contains("？")) {
            complexity += 0.1;
        }

        // 基于特殊字符
        if (queryText.contains("\n") || queryText.contains("\t")) {
            complexity += 0.2;
        }

        // 基于关键词
        if (queryText.contains("为什么") || queryText.contains("如何") || queryText.contains("怎么办")) {
            complexity += 0.2;
        }

        if (queryText.contains("详细") || queryText.contains("具体") || queryText.contains("深入")) {
            complexity += 0.1;
        }

        // 基于意图
        IntentType intent = intentResult.getIntent();
        if (intent == IntentType.CASE_ANALYSIS || intent == IntentType.DECISION) {
            complexity += 0.2;
        }

        // 限制在0-1之间
        return Math.min(1.0, Math.max(0.0, complexity));
    }

    @Override
    public Double calculateThreshold(IntentType intent, Double complexity) {
        // 获取该意图的基础阈值
        Double baseThreshold = INTENT_COMPLEXITY_THRESHOLD.getOrDefault(intent, 0.5);

        // 根据复杂度调整阈值
        if (complexity > 0.7) {
            // 高复杂度，降低阈值（更容易使用LLM）
            return baseThreshold - 0.1;
        } else if (complexity < 0.3) {
            // 低复杂度，提高阈值（更倾向于规则引擎）
            return baseThreshold + 0.1;
        }

        return baseThreshold;
    }
}
