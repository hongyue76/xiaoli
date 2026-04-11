package com.xiaoli.legal.ms.legalcase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaoli.legal.common.core.domain.PageResult;
import com.xiaoli.legal.ms.legalcase.mapper.LegalCaseMapper;
import com.xiaoli.legal.ms.legalcase.model.entity.LegalCase;
import com.xiaoli.legal.ms.legalcase.model.vo.CaseSearchResultVO;
import com.xiaoli.legal.ms.legalcase.model.vo.JudgeProfileVO;
import com.xiaoli.legal.ms.legalcase.service.CaseSearchService;
import com.xiaoli.legal.ms.legalcase.service.JudgeProfileService;
import com.xiaoli.legal.common.ai.service.XiaoliChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 法官画像分析服务实现
 */
@Service
public class JudgeProfileServiceImpl implements JudgeProfileService {

    private static final Logger log = LoggerFactory.getLogger(JudgeProfileServiceImpl.class);

    private final LegalCaseMapper caseMapper;
    private final CaseSearchService caseSearchService;
    private final XiaoliChatService xiaoliChatService;

    public JudgeProfileServiceImpl(LegalCaseMapper caseMapper, CaseSearchService caseSearchService, XiaoliChatService xiaoliChatService) {
        this.caseMapper = caseMapper;
        this.caseSearchService = caseSearchService;
        this.xiaoliChatService = xiaoliChatService;
    }

    @Override
    public JudgeProfileVO analyzeJudgeProfile(String judgeName, String caseType) {
        // 1. 查询该法官的所有案例
        PageResult<CaseSearchResultVO> casesResult = caseSearchService.searchByJudge(
                judgeName, caseType, 1L, 100L);

        List<LegalCase> cases = caseMapper.selectList(
                new LambdaQueryWrapper<LegalCase>()
                        .like(LegalCase::getJudge, judgeName)
                        .eq(caseType != null && !caseType.isEmpty(),
                                LegalCase::getCaseType, caseType)
                        .orderByDesc(LegalCase::getJudgmentDate)
                        .last("LIMIT 100")
        );

        if (cases.isEmpty()) {
            return JudgeProfileVO.builder()
                    .judgeName(judgeName)
                    .totalCaseCount(0)
                    .build();
        }

        // 2. 统计分析
        Map<String, Integer> caseTypeDistribution = analyzeCaseTypeDistribution(cases);
        Map<String, Integer> caseStatusDistribution = analyzeCaseStatusDistribution(cases);
        List<String> commonLegalBasis = extractCommonLegalBasis(cases);

        // 3. 获取典型案例
        List<CaseSearchResultVO> typicalCases = casesResult.getRecords().stream()
                .limit(5)
                .collect(Collectors.toList());

        // 4. 使用AI分析审判风格和倾向
        String judgingStyle = analyzeJudgingStyle(cases);
        String tendencyAnalysis = analyzeTendency(cases);
        String winLoseRatio = analyzeWinLoseRatio(cases);
        String strategyAdvice = generateStrategyAdvice(judgingStyle, tendencyAnalysis, judgeName);
        String precautions = generatePrecautions(judgeName);

        // 5. 获取法官所属法院
        String court = cases.stream()
                .map(LegalCase::getCourt)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("未知");

        return JudgeProfileVO.builder()
                .judgeName(judgeName)
                .court(court)
                .totalCaseCount(cases.size())
                .caseTypeDistribution(caseTypeDistribution)
                .caseStatusDistribution(caseStatusDistribution)
                .judgingStyle(judgingStyle)
                .tendencyAnalysis(tendencyAnalysis)
                .winLoseRatio(winLoseRatio)
                .commonLegalBasis(commonLegalBasis)
                .typicalCases(typicalCases)
                .strategyAdvice(strategyAdvice)
                .precautions(precautions)
                .build();
    }

    /**
     * 分析案件类型分布
     */
    private Map<String, Integer> analyzeCaseTypeDistribution(List<LegalCase> cases) {
        return cases.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getCaseType() != null ? c.getCaseType() : "UNKNOWN",
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
    }

    /**
     * 分析案件状态分布
     */
    private Map<String, Integer> analyzeCaseStatusDistribution(List<LegalCase> cases) {
        return cases.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getCaseStatus() != null ? c.getCaseStatus() : "UNKNOWN",
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
    }

    /**
     * 提取常用法律依据
     */
    private List<String> extractCommonLegalBasis(List<LegalCase> cases) {
        return cases.stream()
                .map(LegalCase::getLegalBasis)
                .filter(Objects::nonNull)
                .filter(basis -> !basis.isEmpty())
                .flatMap(basis -> Arrays.stream(basis.split("[,，]")))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 分析审判风格
     */
    private String analyzeJudgingStyle(List<LegalCase> cases) {
        // 构建案例摘要用于AI分析
        StringBuilder caseSummary = new StringBuilder();
        for (LegalCase c : cases.stream().limit(20).collect(Collectors.toList())) {
            caseSummary.append("案由: ").append(c.getCause()).append("\n");
            caseSummary.append("裁判要旨: ").append(c.getRulingIdea()).append("\n");
            caseSummary.append("裁判结果: ").append(c.getJudgmentResult()).append("\n\n");
        }

        String prompt = "你是一位资深的法律分析师。请根据以下某法官审理的案例摘要，分析该法官的审判风格特点。" +
                "请从以下几个维度进行分析：" +
                "1. 裁判思路（是否偏向严格适用法律还是灵活解释）" +
                "2. 证据采纳倾向（是否严格审查证据还是相对宽松）" +
                "3. 判决倾向（偏向调解还是直接判决）" +
                "4. 量刑/赔偿尺度（严格/适中/宽松）" +
                "请用200字以内的专业分析进行总结。\n\n案例摘要：\n" + caseSummary;

        try {
            return xiaoliChatService.chat(prompt);
        } catch (Exception e) {
            log.warn("AI分析审判风格失败: {}", e.getMessage());
            return "暂无分析数据";
        }
    }

    /**
     * 分析审判倾向
     */
    private String analyzeTendency(List<LegalCase> cases) {
        // 统计原告/被告胜诉情况
        long plaintiffWin = cases.stream()
                .filter(c -> c.getJudgmentResult() != null)
                .filter(c -> c.getJudgmentResult().contains("支持") ||
                        c.getJudgmentResult().contains("胜诉") ||
                        c.getJudgmentResult().contains("准许"))
                .count();

        long defendantWin = cases.stream()
                .filter(c -> c.getJudgmentResult() != null)
                .filter(c -> c.getJudgmentResult().contains("驳回") ||
                        c.getJudgmentResult().contains("不支持") ||
                        c.getJudgmentResult().contains("败诉"))
                .count();

        String prompt = "你是一位资深法律分析师。请根据以下数据分析该法官的审判倾向：" +
                "- 总案件数: " + cases.size() +
                "- 原告/申请方胜诉: " + plaintiffWin +
                "- 被告/被申请方胜诉: " + defendantWin +
                "\n\n请分析该法官在审理案件时的倾向性，包括：" +
                "1. 对原告/被告的态度" +
                "2. 对某类案件的特殊倾向" +
                "3. 是否倾向于调解结案" +
                "请用150字以内的专业分析进行总结。";

        try {
            return xiaoliChatService.chat(prompt);
        } catch (Exception e) {
            log.warn("AI分析审判倾向失败: {}", e.getMessage());
            return "暂无分析数据";
        }
    }

    /**
     * 分析胜败诉比例
     */
    private String analyzeWinLoseRatio(List<LegalCase> cases) {
        long total = cases.size();
        if (total == 0) {
            return "暂无数据";
        }

        long win = cases.stream()
                .filter(c -> c.getJudgmentResult() != null)
                .filter(c -> c.getJudgmentResult().contains("支持") ||
                        c.getJudgmentResult().contains("胜诉") ||
                        c.getJudgmentResult().contains("准许") ||
                        c.getJudgmentResult().contains("部分支持"))
                .count();

        long lose = cases.stream()
                .filter(c -> c.getJudgmentResult() != null)
                .filter(c -> c.getJudgmentResult().contains("驳回") ||
                        c.getJudgmentResult().contains("不支持") ||
                        c.getJudgmentResult().contains("败诉"))
                .count();

        double winRate = total > 0 ? (double) win / total * 100 : 0;
        double loseRate = total > 0 ? (double) lose / total * 100 : 0;

        return String.format("原告胜诉率: %.1f%%, 被告胜诉率: %.1f%%, 其他: %.1f%%",
                winRate, loseRate, 100 - winRate - loseRate);
    }

    /**
     * 生成应对策略建议
     */
    private String generateStrategyAdvice(String judgingStyle, String tendency, String judgeName) {
        String prompt = "你是一位资深诉讼策略专家。请根据以下法官分析信息，为当事人提供应对策略建议：" +
                "审判风格: " + judgingStyle + "\n" +
                "审判倾向: " + tendency + "\n" +
                "法官姓名: " + judgeName + "\n\n" +
                "请提供以下建议：" +
                "1. 举证策略（如何准备证据来迎合法官的证据采纳标准）" +
                "2. 辩论重点（应该强调哪些法律观点）" +
                "3. 调解建议（是否建议调解，如何谈判）" +
                "4. 文书撰写要点（起诉状/答辩状的写作风格建议）" +
                "请用300字以内的专业建议进行总结。";

        try {
            return xiaoliChatService.chat(prompt);
        } catch (Exception e) {
            log.warn("AI生成应对策略失败: {}", e.getMessage());
            return "建议咨询专业律师获取详细策略";
        }
    }

    /**
     * 生成注意事项
     */
    private String generatePrecautions(String judgeName) {
        return "⚠️ 重要提示：" +
                "\n1. 本分析仅供参考，不代表案件实际结果" +
                "\n2. 法官审判风格可能因案件类型、证据情况等因素而变化" +
                "\n3. 建议在专业律师指导下制定诉讼策略" +
                "\n4. " + judgeName + "法官的过往判例可通过中国裁判文书网等渠道进一步查询" +
                "\n5. 请遵守法庭纪律，保持诚信诉讼";
    }
}
