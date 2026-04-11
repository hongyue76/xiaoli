package com.xiaoli.legal.evidence.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 证据分析结果
 */
@TableName("evidence_analysis")
public class EvidenceAnalysis {

    /**
     * 分析ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 证据ID
     */
    private Long evidenceId;

    /**
     * 证据三性分析结果
     */
    private String authenticityAnalysis;

    /**
     * 合法性分析
     */
    private String legalityAnalysis;

    /**
     * 关联性分析
     */
    private String relevanceAnalysis;

    /**
     * 证明力评估: STRONG-强, MEDIUM-中, WEAK-弱
     */
    private String probativeValue;

    /**
     * 证明力评分 (0-100)
     */
    private Integer probativeScore;

    /**
     * 问题描述
     */
    private String issues;

    /**
     * 质证意见
     */
    private String crossExamination;

    /**
     * 补强建议
     */
    private String reinforcement;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEvidenceId() { return evidenceId; }
    public void setEvidenceId(Long evidenceId) { this.evidenceId = evidenceId; }

    public String getAuthenticityAnalysis() { return authenticityAnalysis; }
    public void setAuthenticityAnalysis(String authenticityAnalysis) { this.authenticityAnalysis = authenticityAnalysis; }

    public String getLegalityAnalysis() { return legalityAnalysis; }
    public void setLegalityAnalysis(String legalityAnalysis) { this.legalityAnalysis = legalityAnalysis; }

    public String getRelevanceAnalysis() { return relevanceAnalysis; }
    public void setRelevanceAnalysis(String relevanceAnalysis) { this.relevanceAnalysis = relevanceAnalysis; }

    public String getProbativeValue() { return probativeValue; }
    public void setProbativeValue(String probativeValue) { this.probativeValue = probativeValue; }

    public Integer getProbativeScore() { return probativeScore; }
    public void setProbativeScore(Integer probativeScore) { this.probativeScore = probativeScore; }

    public String getIssues() { return issues; }
    public void setIssues(String issues) { this.issues = issues; }

    public String getCrossExamination() { return crossExamination; }
    public void setCrossExamination(String crossExamination) { this.crossExamination = crossExamination; }

    public String getReinforcement() { return reinforcement; }
    public void setReinforcement(String reinforcement) { this.reinforcement = reinforcement; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
