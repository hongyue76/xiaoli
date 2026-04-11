package com.xiaoli.legal.ms.contract.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaoli.legal.common.core.domain.PageResult;
import com.xiaoli.legal.ms.contract.mapper.ContractIssueMapper;
import com.xiaoli.legal.ms.contract.mapper.ContractMapper;
import com.xiaoli.legal.ms.contract.model.dto.ContractReviewRequest;
import com.xiaoli.legal.ms.contract.model.dto.ContractReviewResponse;
import com.xiaoli.legal.ms.contract.model.entity.Contract;
import com.xiaoli.legal.ms.contract.model.entity.ContractIssue;
import com.xiaoli.legal.ms.contract.service.ContractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 合同服务实现
 */
@Service
public class ContractServiceImpl implements ContractService {

    private static final Logger log = LoggerFactory.getLogger(ContractServiceImpl.class);

    private final ContractMapper contractMapper;
    private final ContractIssueMapper issueMapper;

    public ContractServiceImpl(ContractMapper contractMapper, ContractIssueMapper issueMapper) {
        this.contractMapper = contractMapper;
        this.issueMapper = issueMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ContractReviewResponse reviewContract(ContractReviewRequest request, Long userId) {
        // 保存合同
        Contract contract = new Contract();
        contract.setName(request.getName());
        contract.setContractType(request.getContractType());
        contract.setPartyA(request.getPartyA());
        contract.setPartyB(request.getPartyB());
        contract.setAmount(request.getAmount());
        contract.setDuration(request.getDuration());
        contract.setContent(request.getContent());
        contract.setReviewStatus("REVIEWING");
        contract.setUserId(userId);
        contract.setCreateTime(LocalDateTime.now());
        contract.setUpdateTime(LocalDateTime.now());

        if (StrUtil.isNotBlank(request.getSignDate())) {
            try {
                contract.setSignDate(LocalDateTime.parse(request.getSignDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } catch (Exception e) {
                log.warn("签订日期解析失败: {}", request.getSignDate());
            }
        }

        contractMapper.insert(contract);

        // 执行审查
        ContractReviewResponse response = performReview(contract, request);

        // 保存审查结果
        contract.setReviewStatus("COMPLETED");
        contract.setRiskLevel(response.getRiskLevel());
        contract.setReviewResult(com.alibaba.fastjson2.JSON.toJSONString(response));
        contractMapper.updateById(contract);

        response.setContractId(contract.getId());
        return response;
    }

    @Override
    public ContractReviewResponse reviewByFile(Long fileId, Long userId) {
        // TODO: 根据fileId获取文件内容，进行OCR识别后审查
        return null;
    }

    @Override
    public Contract getContractDetail(Long contractId) {
        return contractMapper.selectById(contractId);
    }

    @Override
    public ContractReviewResponse getReviewResult(Long contractId) {
        Contract contract = contractMapper.selectById(contractId);
        if (contract == null) {
            return null;
        }

        if (StrUtil.isNotBlank(contract.getReviewResult())) {
            return com.alibaba.fastjson2.JSON.parseObject(
                    contract.getReviewResult(),
                    ContractReviewResponse.class
            );
        }

        return null;
    }

    @Override
    public PageResult<Contract> getContractList(Long userId, String status, Long current, Long size) {
        Page<Contract> page = new Page<>(current, size);
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<Contract>()
                .eq(Contract::getUserId, userId)
                .eq(StrUtil.isNotBlank(status), Contract::getReviewStatus, status)
                .orderByDesc(Contract::getCreateTime);

        Page<Contract> result = contractMapper.selectPage(page, wrapper);
        return PageResult.of(result.getCurrent(), result.getSize(), result.getTotal(), result.getRecords());
    }

    @Override
    public void deleteContract(Long contractId) {
        contractMapper.deleteById(contractId);
    }

    @Override
    public List<String> getContractTypes() {
        return List.of(
                "PURCHASE",       // 采购合同
                "SALE",          // 销售合同
                "LEASE",         // 租赁合同
                "EMPLOYMENT",    // 劳动合同
                "SERVICE",       // 服务合同
                "CONSTRUCTION",   // 建设工程合同
                "LOAN",          // 借款合同
                "PARTNERSHIP",   // 合伙合同
                "AGENCY",        // 代理合同
                "CONFIDENTIALITY", // 保密协议
                "COMPETITION",   // 竞业禁止协议
                "OTHER"          // 其他
        );
    }

    /**
     * 执行合同审查
     */
    private ContractReviewResponse performReview(Contract contract, ContractReviewRequest request) {
        List<ContractIssue> issues = new ArrayList<>();

        // 1. 完整性审查
        issues.addAll(checkCompleteness(contract));

        // 2. 合法性审查
        issues.addAll(checkLegality(contract));

        // 3. 公平性审查
        issues.addAll(checkFairness(contract));

        // 4. 风险审查
        issues.addAll(checkRisk(contract));

        // 保存问题
        for (ContractIssue issue : issues) {
            issue.setContractId(contract.getId());
            issueMapper.insert(issue);
        }

        // 计算风险评分
        int riskScore = calculateRiskScore(issues);
        String riskLevel = riskScore >= 70 ? "HIGH" : riskScore >= 40 ? "MEDIUM" : "LOW";

        // 构建响应
        ContractReviewResponse response = ContractReviewResponse.builder()
                .contractId(contract.getId())
                .name(contract.getName())
                .reviewStatus("COMPLETED")
                .riskLevel(riskLevel)
                .riskScore(riskScore)
                .issueCount(ContractReviewResponse.IssueCount.builder()
                        .total(issues.size())
                        .high((int) issues.stream().filter(i -> "HIGH".equals(i.getSeverity())).count())
                        .medium((int) issues.stream().filter(i -> "MEDIUM".equals(i.getSeverity())).count())
                        .low((int) issues.stream().filter(i -> "LOW".equals(i.getSeverity())).count())
                        .build())
                .issues(issues.stream().map(i -> ContractReviewResponse.IssueDetail.builder()
                        .id(i.getId())
                        .title(i.getTitle())
                        .location(i.getLocation())
                        .issueType(i.getIssueType())
                        .severity(i.getSeverity())
                        .description(i.getDescription())
                        .legalBasis(i.getLegalBasis())
                        .suggestion(i.getSuggestion())
                        .build()).collect(Collectors.toList()))
                .dimensionScore(ContractReviewResponse.DimensionScore.builder()
                        .completeness(calculateDimensionScore(issues, "MISSING"))
                        .legality(calculateDimensionScore(issues, "ILLEGAL"))
                        .fairness(calculateDimensionScore(issues, "UNFAIR"))
                        .risk(calculateDimensionScore(issues, "RISK"))
                        .executability(80)
                        .build())
                .aiSummary(generateAISummary(contract, issues))
                .build();

        return response;
    }

    /**
     * 完整性审查
     */
    private List<ContractIssue> checkCompleteness(Contract contract) {
        List<ContractIssue> issues = new ArrayList<>();

        if (StrUtil.isBlank(contract.getPartyA())) {
            issues.add(createIssue("甲方信息缺失", "合同主体", "MISSING", "MEDIUM",
                    "合同缺少甲方(委托方)信息", null, "请补充甲方全称、地址、联系方式等信息"));
        }

        if (StrUtil.isBlank(contract.getPartyB())) {
            issues.add(createIssue("乙方信息缺失", "合同主体", "MISSING", "MEDIUM",
                    "合同缺少乙方(受托方)信息", null, "请补充乙方全称、地址、联系方式等信息"));
        }

        if (contract.getAmount() == null) {
            issues.add(createIssue("金额条款缺失", "合同金额", "MISSING", "HIGH",
                    "合同未约定具体金额或报酬", "《民法典》第五百八十五条", "请明确约定合同金额、支付方式、支付时间"));
        }

        return issues;
    }

    /**
     * 合法性审查
     */
    private List<ContractIssue> checkLegality(Contract contract) {
        List<ContractIssue> issues = new ArrayList<>();

        // 检查违约金条款
        if (StrUtil.isNotBlank(contract.getContent())) {
            if (contract.getContent().contains("违约金") && contract.getContent().contains("30%")) {
                issues.add(createIssue("违约金过高", "违约责任", "ILLEGAL", "HIGH",
                        "违约金约定可能过高", "《民法典》第五百八十五条第二款", "建议将违约金比例调整为实际损失的30%以下"));
            }
        }

        return issues;
    }

    /**
     * 公平性审查
     */
    private List<ContractIssue> checkFairness(Contract contract) {
        List<ContractIssue> issues = new ArrayList<>();

        // 检查是否单方面有利
        if (StrUtil.isNotBlank(contract.getContent())) {
            if (contract.getContent().contains("甲方有权") && !contract.getContent().contains("乙方有权")) {
                issues.add(createIssue("权利义务不对等", "合同权利", "UNFAIR", "MEDIUM",
                        "合同中甲方权利较多，乙方权利较少，可能显失公平", "《民法典》第一百五十一条", "建议增加乙方相应权利条款"));
            }
        }

        return issues;
    }

    /**
     * 风险审查
     */
    private List<ContractIssue> checkRisk(Contract contract) {
        List<ContractIssue> issues = new ArrayList<>();

        // 检查不可抗力条款
        if (StrUtil.isBlank(contract.getContent()) || !contract.getContent().contains("不可抗力")) {
            issues.add(createIssue("缺少不可抗力条款", "免责条款", "MISSING", "MEDIUM",
                    "合同未约定不可抗力条款", "《民法典》第一百八十条", "建议增加不可抗力条款，明确免责情形"));
        }

        // 检查争议解决条款
        if (StrUtil.isBlank(contract.getContent()) || !contract.getContent().contains("争议")) {
            issues.add(createIssue("缺少争议解决条款", "争议解决", "MISSING", "HIGH",
                    "合同未约定争议解决方式", null, "建议约定管辖法院或仲裁机构"));
        }

        return issues;
    }

    /**
     * 创建问题
     */
    private ContractIssue createIssue(String title, String location, String issueType,
                                      String severity, String description, String legalBasis, String suggestion) {
        ContractIssue issue = new ContractIssue();
        issue.setTitle(title);
        issue.setLocation(location);
        issue.setIssueType(issueType);
        issue.setSeverity(severity);
        issue.setDescription(description);
        issue.setLegalBasis(legalBasis);
        issue.setSuggestion(suggestion);
        issue.setCreateTime(LocalDateTime.now());
        return issue;
    }

    /**
     * 计算风险评分
     */
    private int calculateRiskScore(List<ContractIssue> issues) {
        int score = 100;
        for (ContractIssue issue : issues) {
            switch (issue.getSeverity()) {
                case "HIGH":
                    score -= 20;
                    break;
                case "MEDIUM":
                    score -= 10;
                    break;
                case "LOW":
                    score -= 5;
                    break;
            }
        }
        return Math.max(0, score);
    }

    /**
     * 计算维度评分
     */
    private Integer calculateDimensionScore(List<ContractIssue> issues, String issueType) {
        long count = issues.stream().filter(i -> i.getIssueType().equals(issueType)).count();
        return Math.max(0, 100 - (int) (count * 20));
    }

    /**
     * 生成AI总结
     */
    private String generateAISummary(Contract contract, List<ContractIssue> issues) {
        StringBuilder sb = new StringBuilder();
        sb.append("合同审查完成。共发现").append(issues.size()).append("个问题。");

        long highCount = issues.stream().filter(i -> "HIGH".equals(i.getSeverity())).count();
        long mediumCount = issues.stream().filter(i -> "MEDIUM".equals(i.getSeverity())).count();

        if (highCount > 0) {
            sb.append("其中").append(highCount).append("个高风险问题需要重点关注。");
        }

        sb.append("建议尽快根据审查意见修改合同条款。");

        return sb.toString();
    }
}
