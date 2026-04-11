package com.xiaoli.legal.analysis.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 案件分析
 */
@TableName("case_analysis")
public class CaseAnalysis {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long caseId;
    private String caseTitle;
    private String caseType;
    private String analysisType;
    private String content;
    private String conclusion;
    private String suggestions;
    private String relatedCases;
    private String relatedLaws;
    private Integer winProbability;
    private String riskLevel;
    private Long userId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    // Getters
    public Long getId() { return id; }
    public Long getCaseId() { return caseId; }
    public String getCaseTitle() { return caseTitle; }
    public String getCaseType() { return caseType; }
    public String getAnalysisType() { return analysisType; }
    public String getContent() { return content; }
    public String getConclusion() { return conclusion; }
    public String getSuggestions() { return suggestions; }
    public String getRelatedCases() { return relatedCases; }
    public String getRelatedLaws() { return relatedLaws; }
    public Integer getWinProbability() { return winProbability; }
    public String getRiskLevel() { return riskLevel; }
    public Long getUserId() { return userId; }
    public LocalDateTime getCreateTime() { return createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public Integer getDeleted() { return deleted; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }
    public void setCaseTitle(String caseTitle) { this.caseTitle = caseTitle; }
    public void setCaseType(String caseType) { this.caseType = caseType; }
    public void setAnalysisType(String analysisType) { this.analysisType = analysisType; }
    public void setContent(String content) { this.content = content; }
    public void setConclusion(String conclusion) { this.conclusion = conclusion; }
    public void setSuggestions(String suggestions) { this.suggestions = suggestions; }
    public void setRelatedCases(String relatedCases) { this.relatedCases = relatedCases; }
    public void setRelatedLaws(String relatedLaws) { this.relatedLaws = relatedLaws; }
    public void setWinProbability(Integer winProbability) { this.winProbability = winProbability; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
