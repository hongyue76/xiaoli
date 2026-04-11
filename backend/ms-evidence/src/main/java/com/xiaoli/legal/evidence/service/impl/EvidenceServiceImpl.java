package com.xiaoli.legal.evidence.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaoli.legal.evidence.mapper.EvidenceAnalysisMapper;
import com.xiaoli.legal.evidence.mapper.EvidenceMapper;
import com.xiaoli.legal.evidence.model.dto.EvidenceAnalysisRequest;
import com.xiaoli.legal.evidence.model.dto.EvidenceAnalysisResponse;
import com.xiaoli.legal.evidence.model.entity.Evidence;
import com.xiaoli.legal.evidence.model.entity.EvidenceAnalysis;
import com.xiaoli.legal.evidence.service.EvidenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 证据服务实现
 */
@Service
public class EvidenceServiceImpl implements EvidenceService {

    private static final Logger log = LoggerFactory.getLogger(EvidenceServiceImpl.class);

    private final EvidenceMapper evidenceMapper;
    private final EvidenceAnalysisMapper analysisMapper;

    public EvidenceServiceImpl(EvidenceMapper evidenceMapper, EvidenceAnalysisMapper analysisMapper) {
        this.evidenceMapper = evidenceMapper;
        this.analysisMapper = analysisMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EvidenceAnalysisResponse analyzeEvidence(EvidenceAnalysisRequest request, Long userId) {
        List<EvidenceAnalysisResponse.EvidenceResult> results = new ArrayList<>();
        int totalScore = 0;

        // 批量收集待插入的数据
        List<Evidence> evidenceList = new ArrayList<>();
        List<EvidenceAnalysis> analysisList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // 分析每个证据
        for (EvidenceAnalysisRequest.EvidenceItem item : request.getEvidences()) {
            EvidenceAnalysisResponse.EvidenceResult result = analyzeSingleEvidence(item);
            results.add(result);
            totalScore += result.getProbativeScore();

            // 构建证据实体（不立即插入）
            Evidence evidence = new Evidence();
            evidence.setName(item.getName());
            evidence.setEvidenceType(item.getType());
            evidence.setSource(item.getSource());
            evidence.setPurpose(item.getPurpose());
            evidence.setContent(item.getContent());
            evidence.setCaseId(request.getCaseId());
            evidence.setReviewStatus("COMPLETED");
            evidence.setUserId(userId);
            evidence.setCreateTime(now);
            evidence.setUpdateTime(now);
            evidenceList.add(evidence);

            // 构建分析结果实体
            EvidenceAnalysis analysis = new EvidenceAnalysis();
            analysis.setAuthenticityAnalysis(result.getAuthenticity().getAnalysis());
            analysis.setLegalityAnalysis(result.getLegality().getAnalysis());
            analysis.setRelevanceAnalysis(result.getRelevance().getAnalysis());
            analysis.setProbativeValue(result.getProbativeValue());
            analysis.setProbativeScore(result.getProbativeScore());
            analysis.setIssues(com.alibaba.fastjson2.JSON.toJSONString(result.getIssues()));
            analysis.setCrossExamination(result.getCrossExamination());
            analysis.setReinforcement(result.getReinforcement());
            analysis.setCreateTime(now);
            analysisList.add(analysis);
        }

        // 批量插入证据
        if (!evidenceList.isEmpty()) {
            evidenceMapper.batchInsert(evidenceList);
            log.info("批量插入证据 {} 条", evidenceList.size());
        }

        // 关联证据ID后批量插入分析结果
        if (!analysisList.isEmpty()) {
            for (int i = 0; i < analysisList.size(); i++) {
                analysisList.get(i).setEvidenceId(evidenceList.get(i).getId());
            }
            analysisMapper.batchInsert(analysisList);
            log.info("批量插入证据分析结果 {} 条", analysisList.size());
        }

        // 计算整体评分
        int overallScore = results.isEmpty() ? 0 : totalScore / results.size();

        // 构建响应
        return EvidenceAnalysisResponse.builder()
                .totalCount(request.getEvidences().size())
                .chainIntegrity(assessChainIntegrity(results))
                .overallScore(overallScore)
                .riskAssessment(assessRisk(results))
                .results(results)
                .crossExaminationPoints(extractCrossExaminationPoints(results))
                .reinforcementSuggestions(extractReinforcementSuggestions(results))
                .chainSuggestion(generateChainSuggestion(results))
                .build();
    }

    @Override
    public EvidenceAnalysisResponse analyzeByFile(Long fileId, Long userId) {
        // TODO: 实现文件上传分析
        return null;
    }

    @Override
    public Evidence getEvidenceDetail(Long evidenceId) {
        return evidenceMapper.selectById(evidenceId);
    }

    @Override
    public EvidenceAnalysisResponse getAnalysisResult(Long evidenceId) {
        Evidence evidence = evidenceMapper.selectById(evidenceId);
        if (evidence == null) {
            return null;
        }

        EvidenceAnalysis analysis = analysisMapper.selectOne(
                new LambdaQueryWrapper<EvidenceAnalysis>()
                        .eq(EvidenceAnalysis::getEvidenceId, evidenceId)
        );

        if (analysis == null) {
            return null;
        }

        return EvidenceAnalysisResponse.builder()
                .totalCount(1)
                .overallScore(analysis.getProbativeScore())
                .results(List.of(EvidenceAnalysisResponse.EvidenceResult.builder()
                        .name(evidence.getName())
                        .type(evidence.getEvidenceType())
                        .authenticity(EvidenceAnalysisResponse.EvidenceResult.Authenticity.builder()
                                .analysis(analysis.getAuthenticityAnalysis())
                                .build())
                        .legality(EvidenceAnalysisResponse.EvidenceResult.Legality.builder()
                                .analysis(analysis.getLegalityAnalysis())
                                .build())
                        .relevance(EvidenceAnalysisResponse.EvidenceResult.Relevance.builder()
                                .analysis(analysis.getRelevanceAnalysis())
                                .build())
                        .probativeValue(analysis.getProbativeValue())
                        .probativeScore(analysis.getProbativeScore())
                        .crossExamination(analysis.getCrossExamination())
                        .reinforcement(analysis.getReinforcement())
                        .build()))
                .build();
    }

    @Override
    public List<Evidence> getEvidenceList(Long caseId, Long userId) {
        return evidenceMapper.selectList(
                new LambdaQueryWrapper<Evidence>()
                        .eq(caseId != null, Evidence::getCaseId, caseId)
                        .eq(userId != null, Evidence::getUserId, userId)
                        .orderByDesc(Evidence::getCreateTime)
        );
    }

    @Override
    public void deleteEvidence(Long evidenceId) {
        evidenceMapper.deleteById(evidenceId);
    }

    @Override
    public List<String> getEvidenceTypes() {
        return List.of(
                "DOCUMENT",      // 书证
                "OBJECT",       // 物证
                "AUDIO",        // 视听资料
                "ELECTRONIC",   // 电子数据
                "EXPERT",       // 鉴定意见
                "WITNESS",      // 证人证言
                "STATEMENT",    // 当事人陈述
                "INSPECTION"    // 勘验笔录
        );
    }

    /**
     * 分析单个证据
     */
    private EvidenceAnalysisResponse.EvidenceResult analyzeSingleEvidence(EvidenceAnalysisRequest.EvidenceItem item) {
        // 1. 真实性分析
        EvidenceAnalysisResponse.EvidenceResult.Authenticity authenticity = analyzeAuthenticity(item);

        // 2. 合法性分析
        EvidenceAnalysisResponse.EvidenceResult.Legality legality = analyzeLegality(item);

        // 3. 关联性分析
        EvidenceAnalysisResponse.EvidenceResult.Relevance relevance = analyzeRelevance(item);

        // 4. 计算证明力
        int score = calculateProbativeScore(authenticity, legality, relevance);
        String probativeValue = score >= 70 ? "STRONG" : score >= 40 ? "MEDIUM" : "WEAK";

        // 5. 提取问题
        List<String> issues = extractIssues(item, authenticity, legality, relevance);

        // 6. 生成质证意见
        String crossExamination = generateCrossExamination(item, authenticity, legality, relevance);

        // 7. 生成补强建议
        String reinforcement = generateReinforcement(issues, probativeValue);

        return EvidenceAnalysisResponse.EvidenceResult.builder()
                .name(item.getName())
                .type(item.getType())
                .authenticity(authenticity)
                .legality(legality)
                .relevance(relevance)
                .probativeValue(probativeValue)
                .probativeScore(score)
                .issues(issues)
                .crossExamination(crossExamination)
                .reinforcement(reinforcement)
                .build();
    }

    /**
     * 真实性分析
     */
    private EvidenceAnalysisResponse.EvidenceResult.Authenticity analyzeAuthenticity(EvidenceAnalysisRequest.EvidenceItem item) {
        String result;
        String analysis;
        String suggestion = null;

        switch (item.getType()) {
            case "DOCUMENT":
                result = "需要核实";
                analysis = "书证应当核实原件，审查是否存在伪造、篡改的可能。建议要求对方提供原件或经公证的复印件。";
                break;
            case "OBJECT":
                result = "需要核实";
                analysis = "物证应当审查其来源是否合法、是否经过妥善保管、是否存在伪造可能。";
                break;
            case "ELECTRONIC":
                result = "需要核实";
                analysis = "电子数据应当审查其完整性、可信性，是否经过篡改。建议进行取证公证或鉴定。";
                break;
            case "EXPERT":
                result = "可采纳";
                analysis = "鉴定意见由具有资质的专业机构出具，通常具有较高的真实性。";
                break;
            default:
                result = "待审查";
                analysis = "请提供更多证据材料以便分析。";
        }

        return EvidenceAnalysisResponse.EvidenceResult.Authenticity.builder()
                .result(result)
                .analysis(analysis)
                .suggestion(suggestion)
                .build();
    }

    /**
     * 合法性分析
     */
    private EvidenceAnalysisResponse.EvidenceResult.Legality analyzeLegality(EvidenceAnalysisRequest.EvidenceItem item) {
        String result;
        String analysis;
        String suggestion = null;

        if ("WITNESS".equals(item.getType())) {
            result = "需注意";
            analysis = "证人证言需审查证人是否与案件存在利害关系，证言是否前后矛盾。";
            suggestion = "建议申请证人出庭作证，接受质询。";
        } else if ("ELECTRONIC".equals(item.getType())) {
            result = "需注意";
            analysis = "电子数据的收集方式应当合法，非法收集的证据可能不予采纳。";
            suggestion = "建议通过公证或专业机构取证。";
        } else {
            result = "合法";
            analysis = "证据收集方式未发现明显违法情形。";
        }

        return EvidenceAnalysisResponse.EvidenceResult.Legality.builder()
                .result(result)
                .analysis(analysis)
                .suggestion(suggestion)
                .build();
    }

    /**
     * 关联性分析
     */
    private EvidenceAnalysisResponse.EvidenceResult.Relevance analyzeRelevance(EvidenceAnalysisRequest.EvidenceItem item) {
        String result;
        String analysis;

        if (item.getPurpose() != null && !item.getPurpose().isEmpty()) {
            result = "有关联";
            analysis = "该证据与待证事实具有关联性，可以证明" + item.getPurpose() + "。";
        } else {
            result = "关联性不明";
            analysis = "请明确该证据的证明目的，以便更好地分析其关联性。";
        }

        return EvidenceAnalysisResponse.EvidenceResult.Relevance.builder()
                .result(result)
                .analysis(analysis)
                .build();
    }

    /**
     * 计算证明力评分
     */
    private int calculateProbativeScore(
            EvidenceAnalysisResponse.EvidenceResult.Authenticity authenticity,
            EvidenceAnalysisResponse.EvidenceResult.Legality legality,
            EvidenceAnalysisResponse.EvidenceResult.Relevance relevance) {

        int score = 60;

        // 根据真实性调整
        if ("可采纳".equals(authenticity.getResult())) {
            score += 15;
        } else if ("需要核实".equals(authenticity.getResult())) {
            score += 0;
        }

        // 根据合法性调整
        if ("合法".equals(legality.getResult())) {
            score += 15;
        } else if ("需注意".equals(legality.getResult())) {
            score += 5;
        }

        // 根据关联性调整
        if ("有关联".equals(relevance.getResult())) {
            score += 10;
        }

        return Math.min(100, Math.max(0, score));
    }

    /**
     * 提取问题
     */
    private List<String> extractIssues(EvidenceAnalysisRequest.EvidenceItem item,
                                       EvidenceAnalysisResponse.EvidenceResult.Authenticity authenticity,
                                       EvidenceAnalysisResponse.EvidenceResult.Legality legality,
                                       EvidenceAnalysisResponse.EvidenceResult.Relevance relevance) {
        List<String> issues = new ArrayList<>();

        if ("需要核实".equals(authenticity.getResult())) {
            issues.add("证据真实性需要进一步核实");
        }

        if ("需注意".equals(legality.getResult())) {
            issues.add("证据合法性需要关注");
        }

        if ("关联性不明".equals(relevance.getResult())) {
            issues.add("证据关联性不明确，需明确证明目的");
        }

        if (issues.isEmpty()) {
            issues.add("未发现明显问题");
        }

        return issues;
    }

    /**
     * 生成质证意见
     */
    private String generateCrossExamination(EvidenceAnalysisRequest.EvidenceItem item,
                                             EvidenceAnalysisResponse.EvidenceResult.Authenticity authenticity,
                                             EvidenceAnalysisResponse.EvidenceResult.Legality legality,
                                             EvidenceAnalysisResponse.EvidenceResult.Relevance relevance) {
        StringBuilder sb = new StringBuilder();

        sb.append("关于").append(item.getName()).append("的质证意见：\n\n");

        sb.append("1. 真实性：").append(authenticity.getAnalysis()).append("\n");

        sb.append("2. 合法性：").append(legality.getAnalysis()).append("\n");

        sb.append("3. 关联性：").append(relevance.getAnalysis()).append("\n");

        return sb.toString();
    }

    /**
     * 生成补强建议
     */
    private String generateReinforcement(List<String> issues, String probativeValue) {
        if ("STRONG".equals(probativeValue)) {
            return "证据证明力较强，建议保持现有证据完整性，做好证据保全。";
        } else if ("MEDIUM".equals(probativeValue)) {
            return "建议补充其他证据材料，形成证据链，提高证明力。";
        } else {
            return "证据存在较多问题，建议收集更多有力证据，或申请法院调取证据。";
        }
    }

    /**
     * 评估证据链完整性
     */
    private String assessChainIntegrity(List<EvidenceAnalysisResponse.EvidenceResult> results) {
        long strongCount = results.stream().filter(r -> "STRONG".equals(r.getProbativeValue())).count();
        double ratio = (double) strongCount / results.size();

        if (ratio >= 0.7) {
            return "完整";
        } else if (ratio >= 0.4) {
            return "基本完整";
        } else {
            return "不完整";
        }
    }

    /**
     * 评估风险
     */
    private String assessRisk(List<EvidenceAnalysisResponse.EvidenceResult> results) {
        long weakCount = results.stream().filter(r -> "WEAK".equals(r.getProbativeValue())).count();
        if (weakCount >= results.size() / 2) {
            return "高风险";
        } else if (weakCount > 0) {
            return "中等风险";
        } else {
            return "低风险";
        }
    }

    /**
     * 提取质证要点
     */
    private List<String> extractCrossExaminationPoints(List<EvidenceAnalysisResponse.EvidenceResult> results) {
        return results.stream()
                .map(EvidenceAnalysisResponse.EvidenceResult::getCrossExamination)
                .collect(Collectors.toList());
    }

    /**
     * 提取补强建议
     */
    private List<String> extractReinforcementSuggestions(List<EvidenceAnalysisResponse.EvidenceResult> results) {
        return results.stream()
                .map(EvidenceAnalysisResponse.EvidenceResult::getReinforcement)
                .collect(Collectors.toList());
    }

    /**
     * 生成证据链建议
     */
    private String generateChainSuggestion(List<EvidenceAnalysisResponse.EvidenceResult> results) {
        long strongCount = results.stream().filter(r -> "STRONG".equals(r.getProbativeValue())).count();
        if (strongCount >= 3) {
            return "证据链较为完整，建议继续保持和补充。";
        } else {
            return "建议补充更多直接证据，形成完整的证据链条。";
        }
    }
}
