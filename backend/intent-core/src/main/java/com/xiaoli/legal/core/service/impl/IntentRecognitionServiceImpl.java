package com.xiaoli.legal.core.service.impl;

import com.xiaoli.legal.core.IntentType;
import com.xiaoli.legal.core.model.AlternativeIntent;
import com.xiaoli.legal.core.model.IntentResult;
import com.xiaoli.legal.core.model.UserQuery;
import com.xiaoli.legal.core.service.IntentRecognitionService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 意图识别服务实现（基于规则和关键词）
 */
@Service
public class IntentRecognitionServiceImpl implements IntentRecognitionService {

    /**
     * 意图关键词映射
     */
    private static final Map<IntentType, List<KeywordPattern>> INTENT_KEYWORDS = new HashMap<>();

    static {
        // 法律咨询
        INTENT_KEYWORDS.put(IntentType.CONSULT, Arrays.asList(
            new KeywordPattern("法律问题|咨询|解答|请问|如何|怎么办|怎么|法律", 0.8),
            new KeywordPattern("纠纷|赔偿|责任|义务|权利|法律关系", 0.7),
            new KeywordPattern("违反|违法|犯罪|诉讼|起诉|起诉书|判决", 0.6)
        ));

        // 案例检索
        INTENT_KEYWORDS.put(IntentType.CASE_SEARCH, Arrays.asList(
            new KeywordPattern("案例|判例|判决书|裁决|类似案例|相关案例", 0.9),
            new KeywordPattern("查找案例|搜索案例|检索案例|案例检索|案例搜索", 0.85),
            new KeywordPattern("最高法院|最高人民法院|地方法院|中级人民法院", 0.7)
        ));

        // 文书生成
        INTENT_KEYWORDS.put(IntentType.DOCUMENT, Arrays.asList(
            new KeywordPattern("文书|合同书|起诉书|申请书|证明书|委托书", 0.9),
            new KeywordPattern("生成|起草|写|制作|帮我写|起草一份", 0.8),
            new KeywordPattern("合同|协议|遗嘱|授权书|委托书|承诺书", 0.7)
        ));

        // 合同审查
        INTENT_KEYWORDS.put(IntentType.CONTRACT_REVIEW, Arrays.asList(
            new KeywordPattern("审查合同|审核合同|合同审查|合同审核|检查合同", 0.9),
            new KeywordPattern("合同风险|合同条款|合同问题|合同合法", 0.8),
            new KeywordPattern("不公平|霸王条款|合同漏洞|修改合同", 0.7)
        ));

        // 案件分析
        INTENT_KEYWORDS.put(IntentType.CASE_ANALYSIS, Arrays.asList(
            new KeywordPattern("分析案件|案件分析|案情分析|分析案情", 0.9),
            new KeywordPattern("争议焦点|法律适用|诉讼策略|判决理由", 0.8),
            new KeywordPattern("胜诉|败诉|证据分析|事实认定", 0.7)
        ));

        // 证据分析
        INTENT_KEYWORDS.put(IntentType.EVIDENCE_ANALYSIS, Arrays.asList(
            new KeywordPattern("证据|证据分析|证明力|证据效力|举证责任", 0.9),
            new KeywordPattern("真实性|合法性|关联性|证据三性", 0.85),
            new KeywordPattern("证人证言|书证|物证|视听资料|电子证据", 0.7)
        ));

        // 司法决策
        INTENT_KEYWORDS.put(IntentType.DECISION, Arrays.asList(
            new KeywordPattern("量刑|判刑|判决预测|审判|量刑建议", 0.9),
            new KeywordPattern("定罪|量刑幅度|刑期|缓刑|无期徒刑", 0.8),
            new KeywordPattern("风险评估|胜诉概率|败诉风险|判决结果", 0.7)
        ));

        // 合规检查
        INTENT_KEYWORDS.put(IntentType.COMPLIANCE, Arrays.asList(
            new KeywordPattern("合规|合规检查|合规审查|合规风险", 0.9),
            new KeywordPattern("企业合规|公司合规|合规制度|合规体系", 0.8),
            new KeywordPattern("监管|法规|法律法规|监管要求|合规整改", 0.7)
        ));
    }

    @Override
    public IntentResult recognizeIntent(UserQuery query) {
        // 如果用户强制指定意图，直接返回
        if (query.getForceIntent() != null && query.getForceIntent() && query.getExpectedIntent() != null) {
            return IntentResult.builder()
                .intent(query.getExpectedIntent())
                .confidence(1.0)
                .keywords(new String[]{"用户指定"})
                .explanation("用户手动选择的意图")
                .alternatives(new AlternativeIntent[0])
                .build();
        }

        String queryText = query.getQueryText().toLowerCase();
        Map<IntentType, Double> confidenceMap = new HashMap<>();
        Map<IntentType, List<String>> keywordMap = new HashMap<>();

        // 计算每个意图的置信度
        for (Map.Entry<IntentType, List<KeywordPattern>> entry : INTENT_KEYWORDS.entrySet()) {
            IntentType intent = entry.getKey();
            List<KeywordPattern> patterns = entry.getValue();
            double maxConfidence = 0.0;
            List<String> matchedKeywords = new ArrayList<>();

            for (KeywordPattern pattern : patterns) {
                if (Pattern.compile(pattern.pattern).matcher(queryText).find()) {
                    if (pattern.confidence > maxConfidence) {
                        maxConfidence = pattern.confidence;
                    }
                    matchedKeywords.add(pattern.pattern);
                }
            }

            // 如果有匹配，记录置信度和关键词
            if (maxConfidence > 0) {
                confidenceMap.put(intent, maxConfidence);
                keywordMap.put(intent, matchedKeywords);
            }
        }

        // 如果没有匹配的意图，返回未知意图
        if (confidenceMap.isEmpty()) {
            return IntentResult.builder()
                .intent(IntentType.UNKNOWN)
                .confidence(0.0)
                .keywords(new String[]{})
                .explanation("无法识别用户意图")
                .alternatives(new AlternativeIntent[0])
                .build();
        }

        // 找到置信度最高的意图
        IntentType bestIntent = Collections.max(confidenceMap.entrySet(),
            Comparator.comparingDouble(Map.Entry::getValue)).getKey();
        double bestConfidence = confidenceMap.get(bestIntent);

        // 获取备选意图（置信度前3的其他意图）
        List<AlternativeIntent> alternativeList = confidenceMap.entrySet().stream()
            .filter(entry -> !entry.getKey().equals(bestIntent))
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .limit(3)
            .map(entry -> new AlternativeIntent(entry.getKey(), entry.getValue()))
            .collect(java.util.stream.Collectors.toList());
        
        AlternativeIntent[] alternatives = alternativeList.toArray(new AlternativeIntent[0]);

        return IntentResult.builder()
            .intent(bestConfidence >= 0.5 ? bestIntent : IntentType.UNKNOWN)
            .confidence(bestConfidence)
            .keywords(keywordMap.getOrDefault(bestIntent, new ArrayList<>()).toArray(new String[0]))
            .explanation(String.format("根据关键词匹配，识别为%s意图，置信度%.2f",
                bestIntent.getDisplayName(), bestConfidence))
            .alternatives(alternatives)
            .build();
    }

    @Override
    public Double getConfidenceThreshold(IntentType intent) {
        // 默认置信度阈值为0.5
        return 0.5;
    }

    /**
     * 关键词模式
     */
    private static class KeywordPattern {
        String pattern;
        double confidence;

        KeywordPattern(String pattern, double confidence) {
            this.pattern = pattern;
            this.confidence = confidence;
        }
    }
}
