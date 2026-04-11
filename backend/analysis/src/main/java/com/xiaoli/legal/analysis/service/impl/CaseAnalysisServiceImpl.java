package com.xiaoli.legal.analysis.service.impl;

import com.xiaoli.legal.analysis.config.AnalysisConfig;
import com.xiaoli.legal.analysis.mapper.CaseAnalysisMapper;
import com.xiaoli.legal.analysis.model.dto.CaseAnalysisRequest;
import com.xiaoli.legal.analysis.model.dto.CaseAnalysisResponse;
import com.xiaoli.legal.analysis.model.entity.CaseAnalysis;
import com.xiaoli.legal.analysis.service.CaseAnalysisService;
import com.xiaoli.legal.analysis.service.CaseSearchClient;
import com.xiaoli.legal.analysis.service.LitigationFeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 案件分析服务实现
 */
@Service
public class CaseAnalysisServiceImpl implements CaseAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(CaseAnalysisServiceImpl.class);

    private final CaseAnalysisMapper analysisMapper;
    private final CaseSearchClient caseSearchClient;
    private final LitigationFeeService litigationFeeService;
    private final AnalysisConfig analysisConfig;

    public CaseAnalysisServiceImpl(CaseAnalysisMapper analysisMapper, CaseSearchClient caseSearchClient,
                                   LitigationFeeService litigationFeeService, AnalysisConfig analysisConfig) {
        this.analysisMapper = analysisMapper;
        this.caseSearchClient = caseSearchClient;
        this.litigationFeeService = litigationFeeService;
        this.analysisConfig = analysisConfig;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseAnalysisResponse analyzeCase(CaseAnalysisRequest request, Long userId) {
        // 1. 事实梳理
        String factAnalysis = analyzeFacts(request);

        // 2. 法律适用分析
        String lawAnalysis = analyzeLaw(request);

        // 3. 争议焦点提取
        List<String> disputePoints = extractDisputePoints(request);

        // 4. 法律依据
        List<CaseAnalysisResponse.LegalBasis> legalBasis = findLegalBasis(request);

        // 5. 诉讼策略
        String strategy = generateStrategy(request);

        // 6. 风险评估（增强版）
        CaseAnalysisResponse.RiskAssessment riskAssessment = assessRisk(request);

        // 7. 行动建议
        List<String> suggestions = generateSuggestions(request, riskAssessment);

        // 8. 相关案例（从案例库检索）
        List<CaseAnalysisResponse.RelatedCase> relatedCases = findRelatedCases(request);

        // 9. 胜诉概率（增强版）
        int winProbability = calculateWinProbability(request, riskAssessment);

        // 10. 时间节点
        List<CaseAnalysisResponse.TimeNode> timeNodes = generateTimeNodes(request);

        // 11. 诉讼费用计算
        CaseAnalysisResponse.LitigationFee litigationFee = null;
        if (analysisConfig.getFee().isEnabled()) {
            litigationFee = litigationFeeService.calculateFee(request);
        }

        // 12. 证据分析
        CaseAnalysisResponse.EvidenceAnalysis evidenceAnalysis = analyzeEvidence(request);

        // 13. 案由分析
        String causeAnalysis = analyzeCause(request);

        // 14. 法律关系图谱
        List<CaseAnalysisResponse.LegalRelation> legalRelations = analyzeLegalRelations(request);

        // 15. 有利法条筛选
        List<CaseAnalysisResponse.FavorableLegalBasis> favorableLegalBasis = filterFavorableLegalBasis(request, legalBasis, relatedCases);

        // 构建响应
        CaseAnalysisResponse response = CaseAnalysisResponse.builder()
                .caseTitle(request.getCaseTitle())
                .analysisType(request.getAnalysisType())
                .factAnalysis(factAnalysis)
                .lawAnalysis(lawAnalysis)
                .disputePoints(disputePoints)
                .legalBasis(legalBasis)
                .strategy(strategy)
                .riskAssessment(riskAssessment)
                .suggestions(suggestions)
                .relatedCases(relatedCases)
                .winProbability(winProbability)
                .timeNodes(timeNodes)
                .litigationFee(litigationFee)
                .evidenceAnalysis(evidenceAnalysis)
                .causeAnalysis(causeAnalysis)
                .legalRelations(legalRelations)
                .favorableLegalBasis(favorableLegalBasis)
                .build();

        // 保存分析记录
        CaseAnalysis analysis = new CaseAnalysis();
        analysis.setCaseTitle(request.getCaseTitle());
        analysis.setCaseType(request.getCaseType());
        analysis.setAnalysisType(request.getAnalysisType());
        analysis.setContent(factAnalysis + "\n" + lawAnalysis);
        analysis.setConclusion(com.alibaba.fastjson2.JSON.toJSONString(response));
        analysis.setWinProbability(winProbability);
        analysis.setRiskLevel(riskAssessment.getLevel());
        analysis.setUserId(userId);
        analysis.setCreateTime(LocalDateTime.now());
        analysis.setUpdateTime(LocalDateTime.now());
        analysisMapper.insert(analysis);

        response.setAnalysisId(analysis.getId());

        return response;
    }

    @Override
    public CaseAnalysisResponse getAnalysisResult(Long analysisId) {
        CaseAnalysis analysis = analysisMapper.selectById(analysisId);
        if (analysis == null) {
            return null;
        }

        return com.alibaba.fastjson2.JSON.parseObject(
                analysis.getConclusion(),
                CaseAnalysisResponse.class
        );
    }

    @Override
    public List<CaseAnalysis> getAnalysisHistory(Long userId, Long caseId) {
        return analysisMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CaseAnalysis>()
                        .eq(userId != null, CaseAnalysis::getUserId, userId)
                        .eq(caseId != null, CaseAnalysis::getCaseId, caseId)
                        .orderByDesc(CaseAnalysis::getCreateTime)
        );
    }

    @Override
    public void deleteAnalysis(Long analysisId) {
        analysisMapper.deleteById(analysisId);
    }

    /**
     * 事实梳理
     */
    private String analyzeFacts(CaseAnalysisRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("案件事实梳理：\n\n");

        if (request.getOurRole() != null) {
            sb.append("1. 我方角色：").append(request.getOurRole()).append("\n");
        }

        if (request.getOpponent() != null) {
            sb.append("2. 对方当事人：").append(request.getOpponent()).append("\n");
        }

        if (request.getClaims() != null) {
            sb.append("3. 诉讼请求：").append(request.getClaims()).append("\n");
        }

        if (request.getFacts() != null) {
            sb.append("4. 事实与理由：\n").append(request.getFacts()).append("\n");
        }

        if (request.getEvidences() != null && !request.getEvidences().isEmpty()) {
            sb.append("5. 关键证据：\n");
            for (CaseAnalysisRequest.Evidence evidence : request.getEvidences()) {
                sb.append("   - ").append(evidence.getName())
                        .append("（").append(evidence.getType()).append("）")
                        .append("：").append(evidence.getPurpose()).append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * 法律适用分析
     */
    private String analyzeLaw(CaseAnalysisRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("法律适用分析：\n\n");

        // 根据案件类型提供基本法律分析
        switch (request.getCaseType()) {
            case "CONTRACT_DISPUTE":
                sb.append("本案为合同纠纷案件，适用《民法典》合同编相关规定。\n");
                sb.append("重点审查：合同的效力、履行情况、违约责任等。\n");
                break;
            case "MARRIAGE":
                sb.append("本案为婚姻家庭案件，适用《民法典》婚姻家庭编相关规定。\n");
                sb.append("重点审查：夫妻共同财产、子女抚养、债务处理等。\n");
                break;
            case "LABOR_DISPUTE":
                sb.append("本案为劳动争议案件，适用《劳动法》《劳动合同法》相关规定。\n");
                sb.append("重点审查：劳动关系认定、工资支付、加班费、经济补偿等。\n");
                break;
            case "TORT":
                sb.append("本案为侵权案件，适用《民法典》侵权责任编相关规定。\n");
                sb.append("重点审查：侵权行为、损害结果、因果关系、过错程度等。\n");
                break;
            default:
                sb.append("请根据具体案件情况进行分析。\n");
        }

        return sb.toString();
    }

    /**
     * 提取争议焦点
     */
    private List<String> extractDisputePoints(CaseAnalysisRequest request) {
        List<String> points = new ArrayList<>();

        if (request.getFacts() != null) {
            // 简单提取，实际应该使用NLP
            if (request.getFacts().contains("钱") || request.getFacts().contains("款")) {
                points.add("关于款项的争议");
            }
            if (request.getFacts().contains("违约")) {
                points.add("关于是否构成违约的争议");
            }
            if (request.getFacts().contains("责任")) {
                points.add("关于责任承担的争议");
            }
        }

        if (points.isEmpty()) {
            points.add("需要根据案件具体情况确定争议焦点");
        }

        return points;
    }

    /**
     * 查找法律依据
     */
    private List<CaseAnalysisResponse.LegalBasis> findLegalBasis(CaseAnalysisRequest request) {
        List<CaseAnalysisResponse.LegalBasis> bases = new ArrayList<>();

        switch (request.getCaseType()) {
            case "CONTRACT_DISPUTE":
                bases.add(CaseAnalysisResponse.LegalBasis.builder()
                        .lawName("《民法典》")
                        .article("第五百零九条")
                        .content("当事人应当按照约定全面履行自己的义务")
                        .build());
                bases.add(CaseAnalysisResponse.LegalBasis.builder()
                        .lawName("《民法典》")
                        .article("第五百七十七条")
                        .content("当事人一方不履行合同义务或者履行合同义务不符合约定的，应当承担继续履行、采取补救措施或者赔偿损失等违约责任")
                        .build());
                break;
            case "LABOR_DISPUTE":
                bases.add(CaseAnalysisResponse.LegalBasis.builder()
                        .lawName("《劳动合同法》")
                        .article("第三十条")
                        .content("用人单位应当按照劳动合同约定和国家规定，向劳动者及时足额支付劳动报酬")
                        .build());
                break;
            default:
                bases.add(CaseAnalysisResponse.LegalBasis.builder()
                        .lawName("《民事诉讼法》")
                        .article("第一百二十二条")
                        .content("起诉必须符合的条件")
                        .build());
        }

        return bases;
    }

    /**
     * 生成诉讼策略
     */
    private String generateStrategy(CaseAnalysisRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("诉讼策略建议：\n\n");

        // 根据我方角色制定策略
        if ("PLAINTIFF".equals(request.getOurRole())) {
            sb.append("1. 积极举证：整理好所有对我方有利的证据材料。\n");
            sb.append("2. 明确请求：诉讼请求应当明确、具体。\n");
            sb.append("3. 财产保全：必要时申请财产保全。\n");
        } else if ("DEFENDANT".equals(request.getOurRole())) {
            sb.append("1. 仔细审查原告的诉讼请求和证据。\n");
            sb.append("2. 准备答辩状，针对原告主张进行反驳。\n");
            sb.append("3. 必要时提起反诉。\n");
        } else {
            sb.append("1. 全面了解案件情况。\n");
            sb.append("2. 收集整理有利证据。\n");
            sb.append("3. 制定应诉或起诉策略。\n");
        }

        sb.append("4. 考虑和解可能性，准备调解方案。");

        return sb.toString();
    }

    /**
     * 风险评估
     */
    private CaseAnalysisResponse.RiskAssessment assessRisk(CaseAnalysisRequest request) {
        List<CaseAnalysisResponse.RiskItem> items = new ArrayList<>();
        int riskScore = 30; // 基础风险分

        // 评估证据风险
        if (request.getEvidences() == null || request.getEvidences().isEmpty()) {
            items.add(CaseAnalysisResponse.RiskItem.builder()
                    .description("证据不足，可能承担举证不能的风险")
                    .severity("HIGH")
                    .mitigation("建议收集更多证据材料")
                    .build());
            riskScore += 30;
        }

        // 评估诉讼时效风险
        if (request.getFacts() != null && request.getFacts().contains("多年")) {
            items.add(CaseAnalysisResponse.RiskItem.builder()
                    .description("案件时间较长，可能存在诉讼时效风险")
                    .severity("MEDIUM")
                    .mitigation("注意诉讼时效的中断和中止")
                    .build());
            riskScore += 20;
        }

        // 评估对方实力
        if (request.getOpponent() != null) {
            items.add(CaseAnalysisResponse.RiskItem.builder()
                    .description("需要考虑对方的诉讼能力和资源")
                    .severity("LOW")
                    .mitigation("充分准备，积极应对")
                    .build());
            riskScore += 10;
        }

        String level = riskScore >= 70 ? "HIGH" : riskScore >= 40 ? "MEDIUM" : "LOW";

        return CaseAnalysisResponse.RiskAssessment.builder()
                .level(level)
                .score(Math.min(100, riskScore))
                .items(items)
                .build();
    }

    /**
     * 生成建议
     */
    private List<String> generateSuggestions(CaseAnalysisRequest request, CaseAnalysisResponse.RiskAssessment risk) {
        List<String> suggestions = new ArrayList<>();

        suggestions.add("尽快整理和收集相关证据材料");

        if (risk.getLevel().equals("HIGH")) {
            suggestions.add("建议委托专业律师代理本案");
            suggestions.add("注意证据的保全和补强");
        }

        suggestions.add("关注诉讼时效和举证期限");
        suggestions.add("做好调解、和解的准备");

        return suggestions;
    }

    /**
     * 计算胜诉概率
     */
    private int calculateWinProbability(CaseAnalysisRequest request, CaseAnalysisResponse.RiskAssessment risk) {
        int baseProbability = 50;

        // 根据证据情况调整
        if (request.getEvidences() != null && request.getEvidences().size() >= 3) {
            baseProbability += 20;
        } else if (request.getEvidences() != null && request.getEvidences().size() >= 1) {
            baseProbability += 10;
        }

        // 根据风险等级调整
        if ("HIGH".equals(risk.getLevel())) {
            baseProbability -= 20;
        } else if ("LOW".equals(risk.getLevel())) {
            baseProbability += 20;
        }

        return Math.max(10, Math.min(95, baseProbability));
    }

    /**
     * 生成时间节点
     */
    private List<CaseAnalysisResponse.TimeNode> generateTimeNodes(CaseAnalysisRequest request) {
        List<CaseAnalysisResponse.TimeNode> nodes = new ArrayList<>();

        nodes.add(CaseAnalysisResponse.TimeNode.builder()
                .stage("立案阶段")
                .deadline("提交诉状后7日内")
                .action("法院审查并决定是否立案")
                .build());

        nodes.add(CaseAnalysisResponse.TimeNode.builder()
                .stage("举证阶段")
                .deadline("举证期限届满前")
                .action("提交证据材料")
                .build());

        nodes.add(CaseAnalysisResponse.TimeNode.builder()
                .stage("庭审阶段")
                .deadline("法院安排")
                .action("参加庭审，陈述事实和理由")
                .build());

        nodes.add(CaseAnalysisResponse.TimeNode.builder()
                .stage("宣判阶段")
                .deadline("审理结束后")
                .action("领取判决书")
                .build());

        return nodes;
    }

    /**
     * 查找相关案例（增强版：从案例库检索）
     */
    private List<CaseAnalysisResponse.RelatedCase> findRelatedCases(CaseAnalysisRequest request) {
        List<CaseAnalysisResponse.RelatedCase> cases = new ArrayList<>();

        // 从案例库检索
        if (analysisConfig.getAi().isEnabled()) {
            String keyword = request.getCaseTitle();
            if (request.getFacts() != null && request.getFacts().length() > 10) {
                keyword = request.getFacts().substring(0, Math.min(50, request.getFacts().length()));
            }

            List<CaseSearchClient.CaseInfo> searchResults = caseSearchClient.searchRelatedCases(
                    keyword,
                    request.getCaseType(),
                    analysisConfig.getAi().getMaxRelatedCases()
            );

            for (CaseSearchClient.CaseInfo caseInfo : searchResults) {
                cases.add(CaseAnalysisResponse.RelatedCase.builder()
                        .caseId(caseInfo.getId())
                        .title(caseInfo.getTitle())
                        .court(caseInfo.getCourt())
                        .result(caseInfo.getJudgmentResult())
                        .similarity(caseInfo.getScore())
                        .build());
            }
        }

        // 如果没有检索到案例，返回示例
        if (cases.isEmpty()) {
            cases.add(CaseAnalysisResponse.RelatedCase.builder()
                    .caseId(1L)
                    .title("类似案件示例")
                    .court("某市中级人民法院")
                    .result("支持原告诉讼请求")
                    .similarity(0.75)
                    .build());
        }

        return cases;
    }

    /**
     * 证据分析（增强版：含目录化整理、证据能力、法条关联）
     */
    private CaseAnalysisResponse.EvidenceAnalysis analyzeEvidence(CaseAnalysisRequest request) {
        CaseAnalysisResponse.EvidenceAnalysis analysis = new CaseAnalysisResponse.EvidenceAnalysis();
        List<String> advantages = new ArrayList<>();
        List<String> weaknesses = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();
        List<CaseAnalysisResponse.EvidenceCatalog> catalog = new ArrayList<>();
        int score = 50;

        if (request.getEvidences() == null || request.getEvidences().isEmpty()) {
            weaknesses.add("缺少证据材料");
            suggestions.add("建议收集和整理相关证据");
            score -= 30;
        } else {
            // 分析证据数量
            int evidenceCount = request.getEvidences().size();
            if (evidenceCount >= 5) {
                advantages.add("证据材料较为充分");
                score += 15;
            } else if (evidenceCount >= 3) {
                advantages.add("有一定数量的证据支持");
                score += 10;
            } else {
                weaknesses.add("证据数量较少");
                suggestions.add("建议补充更多证据材料");
            }

            // 分析证据类型
            List<String> evidenceTypes = request.getEvidences().stream()
                    .map(CaseAnalysisRequest.Evidence::getType)
                    .toList();

            if (evidenceTypes.contains("合同")) {
                advantages.add("有书面合同作为关键证据");
                score += 10;
            }
            if (evidenceTypes.contains("转账记录") || evidenceTypes.contains("发票")) {
                advantages.add("有财务凭证证明金钱往来");
                score += 10;
            }
            if (evidenceTypes.contains("聊天记录") || evidenceTypes.contains("邮件")) {
                advantages.add("有电子数据作为证据");
                score += 5;
            }
            if (!evidenceTypes.contains("原件") && !evidenceTypes.contains("原始载体")) {
                weaknesses.add("缺乏证据原件或原始载体");
                suggestions.add("尽量提供证据原件或原始载体以增强证明力");
                score -= 10;
            }

            // 生成证据目录（含证据能力和法条关联）
            catalog = buildEvidenceCatalog(request);
        }

        // 确定证据评级
        String level;
        if (score >= 80) {
            level = "A";
        } else if (score >= 60) {
            level = "B";
        } else if (score >= 40) {
            level = "C";
        } else {
            level = "D";
        }

        // 生成证据能力分析摘要
        CaseAnalysisResponse.EvidenceCapabilitySummary capabilitySummary = analyzeEvidenceCapability(request);
        // 生成法条关联摘要
        CaseAnalysisResponse.LawRelationSummary lawRelationSummary = analyzeLawRelation(request, catalog);

        analysis.setScore(Math.max(0, Math.min(100, score)));
        analysis.setLevel(level);
        analysis.setAdvantages(advantages);
        analysis.setWeaknesses(weaknesses);
        analysis.setSuggestions(suggestions);
        analysis.setEvidenceCatalog(catalog);
        analysis.setCapabilitySummary(capabilitySummary);
        analysis.setLawRelationSummary(lawRelationSummary);

        return analysis;
    }

    /**
     * 分析证据能力（证据三性：合法性、真实性、关联性）
     */
    private CaseAnalysisResponse.EvidenceCapabilitySummary analyzeEvidenceCapability(CaseAnalysisRequest request) {
        CaseAnalysisResponse.EvidenceCapabilitySummary summary = new CaseAnalysisResponse.EvidenceCapabilitySummary();
        List<String> legalityAnalysis = new ArrayList<>();
        List<String> authenticityAnalysis = new ArrayList<>();
        List<String> relevanceAnalysis = new ArrayList<>();

        int legalityScore = 50;
        int authenticityScore = 50;
        int relevanceScore = 50;

        if (request.getEvidences() == null || request.getEvidences().isEmpty()) {
            summary.setLegalityScore(0);
            summary.setAuthenticityScore(0);
            summary.setRelevanceScore(0);
            summary.setOverallEvaluation("缺少证据，无法评估");
            summary.setLegalityAnalysis(List.of("缺少证据材料"));
            summary.setAuthenticityAnalysis(List.of("缺少证据材料"));
            summary.setRelevanceAnalysis(List.of("缺少证据材料"));
            return summary;
        }

        for (CaseAnalysisRequest.Evidence evidence : request.getEvidences()) {
            String type = evidence.getType();
            String purpose = evidence.getPurpose();

            // 合法性分析
            if (type != null) {
                if (type.contains("偷拍") || type.contains("偷录") || type.contains("窃听")) {
                    legalityAnalysis.add(evidence.getName() + "可能涉及非法取证，证明力受限");
                    legalityScore -= 10;
                } else if (type.contains("合同") || type.contains("发票") || type.contains("原件")) {
                    legalityAnalysis.add(evidence.getName() + "取证方式合法");
                    legalityScore += 10;
                } else if (type.contains("鉴定") || type.contains("公证")) {
                    legalityAnalysis.add(evidence.getName() + "经第三方认证，合法性较高");
                    legalityScore += 15;
                } else {
                    legalityAnalysis.add(evidence.getName() + "取证方式需进一步核实");
                }
            }

            // 真实性分析
            if (type != null) {
                if (type.contains("原件") || type.contains("原始载体")) {
                    authenticityAnalysis.add(evidence.getName() + "为原件或原始载体，真实性较高");
                    authenticityScore += 15;
                } else if (type.contains("复印件") || type.contains("截图")) {
                    authenticityAnalysis.add(evidence.getName() + "为复制件，需原件核对");
                    authenticityScore -= 5;
                } else if (type.contains("公证书")) {
                    authenticityAnalysis.add(evidence.getName() + "经公证，具有较高真实性");
                    authenticityScore += 15;
                } else if (type.contains("聊天记录") || type.contains("邮件")) {
                    authenticityAnalysis.add(evidence.getName() + "需证明未被篡改，建议进行公证或鉴定");
                    authenticityScore -= 5;
                } else {
                    authenticityAnalysis.add(evidence.getName() + "真实性需进一步核实");
                }
            }

            // 关联性分析
            if (purpose != null && !purpose.isEmpty()) {
                relevanceAnalysis.add(evidence.getName() + "用于证明：" + purpose);
                relevanceScore += 10;
            } else {
                relevanceAnalysis.add(evidence.getName() + "证明目的不明确，需明确");
                relevanceScore -= 5;
            }
        }

        legalityScore = Math.max(0, Math.min(100, legalityScore));
        authenticityScore = Math.max(0, Math.min(100, authenticityScore));
        relevanceScore = Math.max(0, Math.min(100, relevanceScore));

        summary.setLegalityScore(legalityScore);
        summary.setAuthenticityScore(authenticityScore);
        summary.setRelevanceScore(relevanceScore);

        // 综合评价
        int avgScore = (legalityScore + authenticityScore + relevanceScore) / 3;
        String overall;
        if (avgScore >= 80) {
            overall = "证据能力强，三性均满足要求";
        } else if (avgScore >= 60) {
            overall = "证据能力较好，部分环节需加强";
        } else if (avgScore >= 40) {
            overall = "证据能力一般，存在较多瑕疵";
        } else {
            overall = "证据能力较弱，建议补充证据";
        }
        summary.setOverallEvaluation(overall);
        summary.setLegalityAnalysis(legalityAnalysis);
        summary.setAuthenticityAnalysis(authenticityAnalysis);
        summary.setRelevanceAnalysis(relevanceAnalysis);

        return summary;
    }

    /**
     * 分析法条关联
     */
    private CaseAnalysisResponse.LawRelationSummary analyzeLawRelation(CaseAnalysisRequest request,
            List<CaseAnalysisResponse.EvidenceCatalog> catalog) {
        CaseAnalysisResponse.LawRelationSummary summary = new CaseAnalysisResponse.LawRelationSummary();
        List<String> mainLaws = new ArrayList<>();
        List<String> missingLaws = new ArrayList<>();

        String caseType = request.getCaseType();

        // 根据案件类型确定主要法条
        switch (caseType) {
            case "CONTRACT_DISPUTE":
                mainLaws.add("《民法典》第五百零九条 - 合同履行");
                mainLaws.add("《民法典》第五百七十七条 - 违约责任");
                mainLaws.add("《民法典》第五百七十九条 - 金钱债务");
                missingLaws.add("建议补充合同原件及履行凭证");
                break;
            case "LABOR_DISPUTE":
                mainLaws.add("《劳动合同法》第三十条 - 劳动报酬");
                mainLaws.add("《劳动合同法》第四十六条 - 经济补偿");
                mainLaws.add("《劳动合同法》第八十七条 - 赔偿金");
                missingLaws.add("建议补充劳动关系证明材料");
                break;
            case "TORT":
                mainLaws.add("《民法典》第一千一百六十五条 - 过错责任");
                mainLaws.add("《民法典》第一千一百七十九条 - 人身损害赔偿");
                mainLaws.add("《民法典》第一千一百八十四条 - 财产损害赔偿");
                missingLaws.add("建议补充损害结果证明");
                break;
            case "MARRIAGE":
                mainLaws.add("《民法典》第一千零七十九条 - 离婚诉讼");
                mainLaws.add("《民法典》第一千零八十七条 - 财产分割");
                mainLaws.add("《民法典》第一千零八十四条 - 子女抚养");
                missingLaws.add("建议补充财产证明材料");
                break;
            default:
                mainLaws.add("《民事诉讼法》第六十七条 - 举证责任");
                missingLaws.add("请根据具体案件补充相关证据");
        }

        // 统计关联法条总数
        int totalCount = 0;
        for (CaseAnalysisResponse.EvidenceCatalog item : catalog) {
            if (item.getLawRelations() != null) {
                totalCount += item.getLawRelations().size();
            }
        }
        totalCount += mainLaws.size();

        summary.setTotalCount(totalCount);
        summary.setMainLaws(mainLaws);
        summary.setMissingLaws(missingLaws);

        return summary;
    }

    /**
     * 构建证据目录（用于反驳对方，含证据能力和法条关联）
     */
    private List<CaseAnalysisResponse.EvidenceCatalog> buildEvidenceCatalog(CaseAnalysisRequest request) {
        List<CaseAnalysisResponse.EvidenceCatalog> catalog = new ArrayList<>();

        if (request.getEvidences() == null || request.getEvidences().isEmpty()) {
            return catalog;
        }

        int index = 1;
        String ourRole = request.getOurRole();
        String facts = request.getFacts() != null ? request.getFacts() : "";
        String caseType = request.getCaseType();

        for (CaseAnalysisRequest.Evidence evidence : request.getEvidences()) {
            CaseAnalysisResponse.EvidenceCatalog item = CaseAnalysisResponse.EvidenceCatalog.builder()
                    .index(String.valueOf(index++))
                    .name(evidence.getName())
                    .type(evidence.getType())
                    .source(determineEvidenceSource(evidence.getType()))
                    .keyPoint(evidence.getPurpose())
                    .probativeValue(evaluateProbativeValue(evidence.getType()))
                    .status(determineEvidenceStatus(evidence.getType()))
                    .build();

            // 生成反驳对方观点
            String counterArgument = generateCounterArgument(evidence, ourRole, facts);
            item.setCounterArgument(counterArgument);

            // 分析证据能力
            CaseAnalysisResponse.EvidenceCapability capability = analyzeSingleEvidenceCapability(evidence);
            item.setCapability(capability);

            // 关联法条
            List<CaseAnalysisResponse.EvidenceLawRelation> lawRelations = relateLaws(evidence, caseType);
            item.setLawRelations(lawRelations);

            catalog.add(item);
        }

        return catalog;
    }

    /**
     * 分析单个证据的证据能力
     */
    private CaseAnalysisResponse.EvidenceCapability analyzeSingleEvidenceCapability(CaseAnalysisRequest.Evidence evidence) {
        CaseAnalysisResponse.EvidenceCapability capability = new CaseAnalysisResponse.EvidenceCapability();
        String type = evidence.getType();

        // 合法性分析
        if (type != null && (type.contains("偷拍") || type.contains("偷录") || type.contains("窃听"))) {
            capability.setLegality("ILLEGAL");
            capability.setLegalityNote("可能涉及非法取证，需说明取证背景");
        } else if (type != null && (type.contains("合同") || type.contains("发票") || type.contains("原件") || type.contains("公证书"))) {
            capability.setLegality("LEGAL");
            capability.setLegalityNote("取证方式合法，具有证据资格");
        } else {
            capability.setLegality("QUESTIONABLE");
            capability.setLegalityNote("取证方式需进一步核实");
        }

        // 真实性分析
        if (type != null && type.contains("原件")) {
            capability.setAuthenticity("AUTHENTIC");
            capability.setAuthenticityNote("原件真实性较高，可直接采信");
        } else if (type != null && type.contains("公证书")) {
            capability.setAuthenticity("AUTHENTIC");
            capability.setAuthenticityNote("经公证，具有较高真实性");
        } else if (type != null && (type.contains("复印件") || type.contains("截图"))) {
            capability.setAuthenticity("QUESTIONABLE");
            capability.setAuthenticityNote("复制件，需原件核对或补强");
        } else if (type != null && (type.contains("聊天记录") || type.contains("邮件"))) {
            capability.setAuthenticity("QUESTIONABLE");
            capability.setAuthenticityNote("电子数据，需证明未被篡改");
        } else {
            capability.setAuthenticity("QUESTIONABLE");
            capability.setAuthenticityNote("真实性需进一步核实");
        }

        // 关联性分析
        String purpose = evidence.getPurpose();
        if (purpose != null && !purpose.isEmpty()) {
            capability.setRelevance("HIGH");
            capability.setRelevanceNote("证据与待证事实直接相关");
        } else {
            capability.setRelevance("MEDIUM");
            capability.setRelevanceNote("需明确证明目的以确定关联性");
        }

        // 综合评价
        int score = 0;
        if ("LEGAL".equals(capability.getLegality())) score += 33;
        if ("AUTHENTIC".equals(capability.getAuthenticity())) score += 33;
        if ("HIGH".equals(capability.getRelevance())) score += 34;

        if (score >= 80) {
            capability.setCapabilityLevel("A - 证据能力强");
        } else if (score >= 50) {
            capability.setCapabilityLevel("B - 证据能力较好");
        } else if (score >= 30) {
            capability.setCapabilityLevel("C - 证据能力一般");
        } else {
            capability.setCapabilityLevel("D - 证据能力弱");
        }

        return capability;
    }

    /**
     * 证据关联法条
     */
    private List<CaseAnalysisResponse.EvidenceLawRelation> relateLaws(CaseAnalysisRequest.Evidence evidence, String caseType) {
        List<CaseAnalysisResponse.EvidenceLawRelation> relations = new ArrayList<>();
        String type = evidence.getType();

        switch (caseType) {
            case "CONTRACT_DISPUTE":
                if (type != null && type.contains("合同")) {
                    relations.add(CaseAnalysisResponse.EvidenceLawRelation.builder()
                            .lawName("《民法典》")
                            .article("第五百零九条")
                            .relationNote("证明合同义务的履行情况")
                            .priority("HIGH")
                            .build());
                    relations.add(CaseAnalysisResponse.EvidenceLawRelation.builder()
                            .lawName("《民法典》")
                            .article("第五百七十七条")
                            .relationNote("证明对方存在违约行为")
                            .priority("HIGH")
                            .build());
                }
                if (type != null && (type.contains("转账") || type.contains("流水"))) {
                    relations.add(CaseAnalysisResponse.EvidenceLawRelation.builder()
                            .lawName("《民法典》")
                            .article("第五百七十九条")
                            .relationNote("证明金钱债务的履行情况")
                            .priority("HIGH")
                            .build());
                }
                if (type != null && (type.contains("聊天") || type.contains("邮件"))) {
                    relations.add(CaseAnalysisResponse.EvidenceLawRelation.builder()
                            .lawName("《民法典》")
                            .article("第四百六十九条")
                            .relationNote("证明合同订立和变更内容")
                            .priority("MEDIUM")
                            .build());
                }
                break;

            case "LABOR_DISPUTE":
                if (type != null && (type.contains("合同") || type.contains("劳动合同"))) {
                    relations.add(CaseAnalysisResponse.EvidenceLawRelation.builder()
                            .lawName("《劳动合同法》")
                            .article("第十条")
                            .relationNote("证明劳动关系的存在")
                            .priority("HIGH")
                            .build());
                }
                if (type != null && (type.contains("工资") || type.contains("流水") || type.contains("转账"))) {
                    relations.add(CaseAnalysisResponse.EvidenceLawRelation.builder()
                            .lawName("《劳动合同法》")
                            .article("第三十条")
                            .relationNote("证明劳动报酬的支付情况")
                            .priority("HIGH")
                            .build());
                }
                if (type != null && (type.contains("聊天") || type.contains("通知") || type.contains("邮件"))) {
                    relations.add(CaseAnalysisResponse.EvidenceLawRelation.builder()
                            .lawName("《劳动合同法》")
                            .article("第八十七条")
                            .relationNote("证明违法解除劳动关系")
                            .priority("HIGH")
                            .build());
                }
                break;

            case "TORT":
                if (type != null && (type.contains("合同") || type.contains("协议"))) {
                    relations.add(CaseAnalysisResponse.EvidenceLawRelation.builder()
                            .lawName("《民法典》")
                            .article("第一千一百六十五条")
                            .relationNote("证明侵权事实的存在")
                            .priority("HIGH")
                            .build());
                }
                if (type != null && (type.contains("诊断") || type.contains("医疗") || type.contains("发票"))) {
                    relations.add(CaseAnalysisResponse.EvidenceLawRelation.builder()
                            .lawName("《民法典》")
                            .article("第一千一百七十九条")
                            .relationNote("证明损害结果及赔偿金额")
                            .priority("HIGH")
                            .build());
                }
                if (type != null && (type.contains("鉴定") || type.contains("评估"))) {
                    relations.add(CaseAnalysisResponse.EvidenceLawRelation.builder()
                            .lawName("《民法典》")
                            .article("第一千一百六十六条")
                            .relationNote("证明因果关系和过错程度")
                            .priority("MEDIUM")
                            .build());
                }
                break;

            case "MARRIAGE":
                if (type != null && (type.contains("结婚") || type.contains("离婚"))) {
                    relations.add(CaseAnalysisResponse.EvidenceLawRelation.builder()
                            .lawName("《民法典》")
                            .article("第一千零四十一条")
                            .relationNote("证明婚姻关系的有效性")
                            .priority("HIGH")
                            .build());
                }
                if (type != null && (type.contains("财产") || type.contains("房产") || type.contains("车辆"))) {
                    relations.add(CaseAnalysisResponse.EvidenceLawRelation.builder()
                            .lawName("《民法典》")
                            .article("第一千零八十七条")
                            .relationNote("证明夫妻共同财产情况")
                            .priority("HIGH")
                            .build());
                }
                if (type != null && (type.contains("子女") || type.contains("出生"))) {
                    relations.add(CaseAnalysisResponse.EvidenceLawRelation.builder()
                            .lawName("《民法典》")
                            .article("第一千零八十四条")
                            .relationNote("证明子女抚养的相关事实")
                            .priority("MEDIUM")
                            .build());
                }
                break;

            default:
                relations.add(CaseAnalysisResponse.EvidenceLawRelation.builder()
                        .lawName("《民事诉讼法》")
                        .article("第六十七条")
                        .relationNote("证明案件相关事实")
                        .priority("MEDIUM")
                        .build());
        }

        // 如果没有匹配到法条，添加默认
        if (relations.isEmpty()) {
            relations.add(CaseAnalysisResponse.EvidenceLawRelation.builder()
                    .lawName("《民事诉讼法》")
                    .article("第六十七条")
                    .relationNote("根据证据内容确定具体适用法条")
                    .priority("LOW")
                    .build());
        }

        return relations;
    }

    /**
     * 判断证据来源
     */
    private String determineEvidenceSource(String evidenceType) {
        if (evidenceType == null) {
            return "自行收集";
        }
        if (evidenceType.contains("合同") || evidenceType.contains("发票") || evidenceType.contains("收据")) {
            return "我方持有";
        } else if (evidenceType.contains("聊天记录") || evidenceType.contains("邮件") || evidenceType.contains("短信")) {
            return "电子数据";
        } else if (evidenceType.contains("鉴定") || evidenceType.contains("评估")) {
            return "第三方机构";
        } else if (evidenceType.contains("判决") || evidenceType.contains("裁定")) {
            return "法院文书";
        }
        return "自行收集";
    }

    /**
     * 评估证明力
     */
    private String evaluateProbativeValue(String evidenceType) {
        if (evidenceType == null) {
            return "待核实";
        }
        if (evidenceType.contains("原件") || evidenceType.contains("原始载体")) {
            return "证明力强";
        } else if (evidenceType.contains("合同") || evidenceType.contains("公证书")) {
            return "证明力较强";
        } else if (evidenceType.contains("转账记录") || evidenceType.contains("发票")) {
            return "证明力较强";
        } else if (evidenceType.contains("复印件") || evidenceType.contains("截图")) {
            return "证明力较弱，需补强";
        } else if (evidenceType.contains("聊天记录") || evidenceType.contains("邮件")) {
            return "需证明真实性";
        }
        return "证明力一般";
    }

    /**
     * 确定证据状态
     */
    private String determineEvidenceStatus(String evidenceType) {
        if (evidenceType == null) {
            return "待确认";
        }
        if (evidenceType.contains("原件")) {
            return "原件";
        } else if (evidenceType.contains("复印件")) {
            return "复印件（需原件核对）";
        } else if (evidenceType.contains("电子数据") || evidenceType.contains("聊天") || evidenceType.contains("邮件")) {
            return "电子数据（需原始载体）";
        } else if (evidenceType.contains("截图")) {
            return "截图（需原始载体核对）";
        }
        return "已收集";
    }

    /**
     * 生成反驳对方观点
     */
    private String generateCounterArgument(CaseAnalysisRequest.Evidence evidence, String ourRole, String facts) {
        StringBuilder sb = new StringBuilder();
        String type = evidence.getType();
        String purpose = evidence.getPurpose();

        if ("PLAINTIFF".equals(ourRole)) {
            // 我方是原告
            if (type != null && type.contains("合同")) {
                sb.append("该合同书证明了双方权利义务关系，可反驳对方关于合同效力或履行情况的异议。");
            } else if (type != null && (type.contains("转账") || type.contains("流水"))) {
                sb.append("转账记录证明了款项支付事实，可反驳对方关于未收到款项的抗辩。");
            } else if (type != null && type.contains("聊天")) {
                sb.append("聊天记录证明了双方沟通情况，可反驳对方关于事实不存在的陈述。");
            } else if (purpose != null) {
                sb.append("该证据用于证明：").append(purpose).append("，可据此反驳对方相应抗辩。");
            }
        } else if ("DEFENDANT".equals(ourRole)) {
            // 我方是被告
            if (type != null && type.contains("合同")) {
                sb.append("合同条款可证明我方已履行义务或对方存在违约行为。");
            } else if (type != null && type.contains("聊天")) {
                sb.append("聊天记录可证明对方已确认或变更相关内容。");
            } else if (purpose != null) {
                sb.append("该证据用于反驳原告主张：").append(purpose).append("。");
            }
        } else {
            if (purpose != null) {
                sb.append("该证据可证明：").append(purpose);
            }
        }

        if (sb.length() == 0) {
            sb.append("该证据可作为主张的依据，根据具体案件情况组织质证意见。");
        }

        return sb.toString();
    }

    /**
     * 案由分析
     */
    private String analyzeCause(CaseAnalysisRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("案由分析：\n\n");

        String caseType = request.getCaseType();
        switch (caseType) {
            case "CONTRACT_DISPUTE":
                sb.append("本案案由为合同纠纷。");
                sb.append("根据《民事诉讼法》相关规定，合同纠纷由被告住所地或合同履行地人民法院管辖。");
                sb.append("建议确定管辖法院时优先考虑合同履行地。");
                break;
            case "MARRIAGE":
                sb.append("本案案由为婚姻家庭纠纷。");
                sb.append("此类案件由被告住所地人民法院管辖。");
                break;
            case "LABOR_DISPUTE":
                sb.append("本案案由为劳动争议。");
                sb.append("劳动争议案件由劳动合同履行地或用人单位所在地劳动争议仲裁委员会管辖。");
                sb.append("需先经过劳动仲裁程序。");
                break;
            case "TORT":
                sb.append("本案案由为侵权责任纠纷。");
                sb.append("由侵权行为地或被告住所地人民法院管辖。");
                break;
            default:
                sb.append("请根据具体案件情况确定案由。");
        }

        return sb.toString();
    }

    /**
     * 法律关系分析
     */
    private List<CaseAnalysisResponse.LegalRelation> analyzeLegalRelations(CaseAnalysisRequest request) {
        List<CaseAnalysisResponse.LegalRelation> relations = new ArrayList<>();

        // 添加基本法律关系
        if (request.getOurRole() != null) {
            CaseAnalysisResponse.LegalRelation relation = new CaseAnalysisResponse.LegalRelation();
            relation.setType("诉讼地位");
            relation.setDescription("我方作为" + request.getOurRole());
            List<String> parties = new ArrayList<>();
            parties.add("我方");
            if (request.getOpponent() != null) {
                parties.add(request.getOpponent());
            }
            relation.setParties(parties);
            relations.add(relation);
        }

        // 根据案件类型添加法律关系
        String caseType = request.getCaseType();
        switch (caseType) {
            case "CONTRACT_DISPUTE":
                relations.add(CaseAnalysisResponse.LegalRelation.builder()
                        .type("合同关系")
                        .description("合同当事人之间的权利义务关系")
                        .parties(List.of("原告", "被告"))
                        .build());
                break;
            case "TORT":
                relations.add(CaseAnalysisResponse.LegalRelation.builder()
                        .type("侵权关系")
                        .description("侵权行为导致的损害赔偿关系")
                        .parties(List.of("侵权人", "被侵权人"))
                        .build());
                break;
            case "LABOR_DISPUTE":
                relations.add(CaseAnalysisResponse.LegalRelation.builder()
                        .type("劳动关系")
                        .description("用人单位与劳动者之间的劳动争议")
                        .parties(List.of("劳动者", "用人单位"))
                        .build());
                break;
        }

        return relations;
    }

    /**
     * 筛选有利法条
     */
    private List<CaseAnalysisResponse.FavorableLegalBasis> filterFavorableLegalBasis(
            CaseAnalysisRequest request,
            List<CaseAnalysisResponse.LegalBasis> legalBasisList,
            List<CaseAnalysisResponse.RelatedCase> relatedCases) {

        List<CaseAnalysisResponse.FavorableLegalBasis> favorableList = new ArrayList<>();
        String caseType = request.getCaseType();
        String ourRole = request.getOurRole();
        String facts = request.getFacts() != null ? request.getFacts() : "";

        // 根据案件类型筛选有利法条
        switch (caseType) {
            case "CONTRACT_DISPUTE":
                // 合同纠纷 - 我方是原告时
                if ("PLAINTIFF".equals(ourRole)) {
                    favorableList.add(CaseAnalysisResponse.FavorableLegalBasis.builder()
                            .lawName("《民法典》")
                            .article("第五百零九条")
                            .content("当事人应当按照约定全面履行自己的义务")
                            .applicableScenario("对方未按合同约定履行义务")
                            .favorableReason("作为守约方，有权要求对方继续履行合同并承担违约责任")
                            .supportingCases(extractCaseTitles(relatedCases, 2))
                            .difficulty("EASY")
                            .build());

                    favorableList.add(CaseAnalysisResponse.FavorableLegalBasis.builder()
                            .lawName("《民法典》")
                            .article("第五百七十七条")
                            .content("当事人一方不履行合同义务或者履行合同义务不符合约定的，应当承担继续履行、采取补救措施或者赔偿损失等违约责任")
                            .applicableScenario("对方存在违约行为")
                            .favorableReason("明确对方违约责任，可主张违约金或赔偿损失")
                            .supportingCases(extractCaseTitles(relatedCases, 2))
                            .difficulty("EASY")
                            .build());

                    // 如果涉及付款
                    if (facts.contains("付款") || facts.contains("支付") || facts.contains("钱")) {
                        favorableList.add(CaseAnalysisResponse.FavorableLegalBasis.builder()
                                .lawName("《民法典》")
                                .article("第五百七十九条")
                                .content("当事人一方未支付价款、报酬、租金、利息，或者不履行其他金钱债务的，对方可以请求其支付")
                                .applicableScenario("对方未支付合同款项")
                                .favorableReason("可直接请求对方支付金钱债务")
                                .supportingCases(extractCaseTitles(relatedCases, 2))
                                .difficulty("EASY")
                                .build());
                    }
                } else {
                    // 我方是被告
                    favorableList.add(CaseAnalysisResponse.FavorableLegalBasis.builder()
                            .lawName("《民法典》")
                            .article("第五百八十五条")
                            .content("约定的违约金低于造成的损失的，人民法院或者仲裁机构可以根据当事人的请求予以增加")
                            .applicableScenario("对方主张的违约金过高")
                            .favorableReason("可以请求法院适当减少违约金")
                            .supportingCases(extractCaseTitles(relatedCases, 2))
                            .difficulty("MEDIUM")
                            .build());
                }
                break;

            case "LABOR_DISPUTE":
                // 劳动争议
                if (facts.contains("工资") || facts.contains("报酬") || facts.contains("加班")) {
                    favorableList.add(CaseAnalysisResponse.FavorableLegalBasis.builder()
                            .lawName("《劳动合同法》")
                            .article("第三十条")
                            .content("用人单位应当按照劳动合同约定和国家规定，向劳动者及时足额支付劳动报酬")
                            .applicableScenario("用人单位拖欠工资或加班费")
                            .favorableReason("劳动者有权追讨工资报酬，法律规定明确，胜诉率较高")
                            .supportingCases(extractCaseTitles(relatedCases, 2))
                            .difficulty("EASY")
                            .build());
                }

                if (facts.contains("违法解除") || facts.contains("辞退") || facts.contains("开除")) {
                    favorableList.add(CaseAnalysisResponse.FavorableLegalBasis.builder()
                            .lawName("《劳动合同法》")
                            .article("第八十七条")
                            .content("用人单位违反本法规定解除或者终止劳动合同的，应当依照本法第四十七条规定的经济补偿标准的二倍向劳动者支付赔偿金")
                            .applicableScenario("用人单位违法解除劳动合同")
                            .favorableReason("可主张双倍经济补偿金作为赔偿金")
                            .supportingCases(extractCaseTitles(relatedCases, 2))
                            .difficulty("EASY")
                            .build());
                }

                if (facts.contains("经济补偿") || facts.contains("补偿金")) {
                    favorableList.add(CaseAnalysisResponse.FavorableLegalBasis.builder()
                            .lawName("《劳动合同法》")
                            .article("第四十六条")
                            .content("有下列情形之一的，用人单位应当向劳动者支付经济补偿：...")
                            .applicableScenario("符合支付经济补偿的情形")
                            .favorableReason("明确经济补偿的计算标准和依据")
                            .supportingCases(extractCaseTitles(relatedCases, 2))
                            .difficulty("MEDIUM")
                            .build());
                }
                break;

            case "TORT":
                // 侵权纠纷
                favorableList.add(CaseAnalysisResponse.FavorableLegalBasis.builder()
                        .lawName("《民法典》")
                        .article("第一千一百六十五条")
                        .content("行为人因过错侵害他人民事权益造成损害的，应当承担侵权责任")
                        .applicableScenario("对方存在过错侵权行为")
                        .favorableReason("过错责任原则，有利于证明对方责任")
                        .supportingCases(extractCaseTitles(relatedCases, 2))
                        .difficulty("MEDIUM")
                        .build());

                if (facts.contains("人身") || facts.contains("伤") || facts.contains("医疗")) {
                    favorableList.add(CaseAnalysisResponse.FavorableLegalBasis.builder()
                            .lawName("《民法典》")
                            .article("第一千一百七十九条")
                            .content("侵害他人造成人身损害的，应当赔偿医疗费、护理费、交通费、营养费、住院伙食补助费等为治疗和康复支出的合理费用，以及因误工减少的收入")
                            .applicableScenario("造成人身损害要求赔偿")
                            .favorableReason("明确规定了人身损害赔偿的范围")
                            .supportingCases(extractCaseTitles(relatedCases, 2))
                            .difficulty("EASY")
                            .build());
                }

                if (facts.contains("财产") || facts.contains("损失")) {
                    favorableList.add(CaseAnalysisResponse.FavorableLegalBasis.builder()
                            .lawName("《民法典》")
                            .article("第一千一百八十四条")
                            .content("侵害他人财产的，财产损失按照损失发生时的市场价格或者其他合理方式计算")
                            .applicableScenario("财产损害赔偿")
                            .favorableReason("提供了财产损失的计算方式")
                            .supportingCases(extractCaseTitles(relatedCases, 2))
                            .difficulty("EASY")
                            .build());
                }
                break;

            case "MARRIAGE":
                // 婚姻家庭
                if (facts.contains("离婚")) {
                    favorableList.add(CaseAnalysisResponse.FavorableLegalBasis.builder()
                            .lawName("《民法典》")
                            .article("第一千零七十九条")
                            .content("夫妻一方要求离婚的，可以由有关组织进行调解或者直接向人民法院提起离婚诉讼")
                            .applicableScenario("主张离婚")
                            .favorableReason("明确了离婚诉讼的法定途径")
                            .supportingCases(extractCaseTitles(relatedCases, 2))
                            .difficulty("MEDIUM")
                            .build());
                }

                if (facts.contains("财产") || facts.contains("分割")) {
                    favorableList.add(CaseAnalysisResponse.FavorableLegalBasis.builder()
                            .lawName("《民法典》")
                            .article("第一千零八十七条")
                            .content("离婚时，夫妻的共同财产由双方协议处理；协议不成的，由人民法院根据财产的具体情况，按照照顾子女、女方和无过错方权益的原则判决")
                            .applicableScenario("夫妻共同财产分割")
                            .favorableReason("明确了财产分割原则，有利于保护我方权益")
                            .supportingCases(extractCaseTitles(relatedCases, 2))
                            .difficulty("MEDIUM")
                            .build());
                }

                if (facts.contains("抚养") || facts.contains("子女")) {
                    favorableList.add(CaseAnalysisResponse.FavorableLegalBasis.builder()
                            .lawName("《民法典》")
                            .article("第一千零八十四条")
                            .content("父母与子女间的关系，不因父母离婚而消除。离婚后，子女无论由父或者母直接抚养，仍是父母双方的子女")
                            .applicableScenario("子女抚养权争议")
                            .favorableReason("明确了父母对子女的抚养义务不因离婚而消除")
                            .supportingCases(extractCaseTitles(relatedCases, 2))
                            .difficulty("MEDIUM")
                            .build());
                }
                break;

            default:
                // 默认增加一些通用有利法条
                favorableList.add(CaseAnalysisResponse.FavorableLegalBasis.builder()
                        .lawName("《民事诉讼法》")
                        .article("第六十七条")
                        .content("当事人对自己提出的主张，有责任提供证据")
                        .applicableScenario("需要证明自己的主张")
                        .favorableReason("明确举证责任分配")
                        .supportingCases(extractCaseTitles(relatedCases, 1))
                        .difficulty("EASY")
                        .build());
        }

        // 如果没有找到有利法条，添加默认
        if (favorableList.isEmpty()) {
            favorableList.add(CaseAnalysisResponse.FavorableLegalBasis.builder()
                    .lawName("《民事诉讼法》")
                    .article("第一百二十二条")
                    .content("起诉必须符合下列条件：(一)原告是与本案有直接利害关系的公民、法人和其他组织；(二)有明确的被告；(三)有具体的诉讼请求和事实、理由；(四)属于人民法院受理民事诉讼的范围和受诉人民法院管辖")
                    .applicableScenario("基本诉讼条件")
                    .favorableReason("明确起诉的基本条件")
                    .supportingCases(extractCaseTitles(relatedCases, 1))
                    .difficulty("EASY")
                    .build());
        }

        return favorableList;
    }

    /**
     * 从相关案例中提取案例标题
     */
    private List<String> extractCaseTitles(List<CaseAnalysisResponse.RelatedCase> cases, int maxCount) {
        if (cases == null || cases.isEmpty()) {
            return List.of("类似判例支持");
        }
        return cases.stream()
                .limit(maxCount)
                .map(CaseAnalysisResponse.RelatedCase::getTitle)
                .toList();
    }
}
