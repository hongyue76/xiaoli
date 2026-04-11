package com.xiaoli.legal.ms.contract.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 合同问题条款
 */
@TableName("contract_issue")
public class ContractIssue {

    /**
     * 问题ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 条款标题
     */
    private String title;

    /**
     * 条款位置
     */
    private String location;

    /**
     * 原条款内容
     */
    private String originalContent;

    /**
     * 问题类型: MISSING-缺失, AMBIGUOUS-歧义, UNFAIR-不公平, RISK-风险, ILLEGAL-违法
     */
    private String issueType;

    /**
     * 风险等级: LOW-低, MEDIUM-中, HIGH-高
     */
    private String severity;

    /**
     * 问题描述
     */
    private String description;

    /**
     * 法律依据
     */
    private String legalBasis;

    /**
     * 修改建议
     */
    private String suggestion;

    /**
     * AI建议内容
     */
    private String aiSuggestion;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getOriginalContent() { return originalContent; }
    public void setOriginalContent(String originalContent) { this.originalContent = originalContent; }

    public String getIssueType() { return issueType; }
    public void setIssueType(String issueType) { this.issueType = issueType; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLegalBasis() { return legalBasis; }
    public void setLegalBasis(String legalBasis) { this.legalBasis = legalBasis; }

    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }

    public String getAiSuggestion() { return aiSuggestion; }
    public void setAiSuggestion(String aiSuggestion) { this.aiSuggestion = aiSuggestion; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
