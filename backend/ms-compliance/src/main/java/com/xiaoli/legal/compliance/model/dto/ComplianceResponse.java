package com.xiaoli.legal.compliance.model.dto;

import java.util.List;
import java.util.Map;

/**
 * 合规审查响应DTO
 */
public class ComplianceResponse {
    
    /** 审查ID */
    private Long reviewId;
    
    /** 审查类型 */
    private String reviewType;
    
    /** 审查结果摘要 */
    private String summary;
    
    /** 风险等级 */
    private String riskLevel;
    
    /** 风险评分 */
    private Integer riskScore;
    
    /** 发现问题列表 */
    private List<IssueInfo> issues;
    
    /** 建议措施列表 */
    private List<SuggestionInfo> suggestions;
    
    /** 合规评分 */
    private Integer complianceScore;
    
    /** 风险分布 */
    private Map<String, Integer> riskDistribution;
    
    /** 审查结论 */
    private String conclusion;
    
    /** 法律依据 */
    private List<String> legalBasis;

    // Getters and Setters
    public Long getReviewId() { return reviewId; }
    public void setReviewId(Long reviewId) { this.reviewId = reviewId; }

    public String getReviewType() { return reviewType; }
    public void setReviewType(String reviewType) { this.reviewType = reviewType; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }

    public List<IssueInfo> getIssues() { return issues; }
    public void setIssues(List<IssueInfo> issues) { this.issues = issues; }

    public List<SuggestionInfo> getSuggestions() { return suggestions; }
    public void setSuggestions(List<SuggestionInfo> suggestions) { this.suggestions = suggestions; }

    public Integer getComplianceScore() { return complianceScore; }
    public void setComplianceScore(Integer complianceScore) { this.complianceScore = complianceScore; }

    public Map<String, Integer> getRiskDistribution() { return riskDistribution; }
    public void setRiskDistribution(Map<String, Integer> riskDistribution) { this.riskDistribution = riskDistribution; }

    public String getConclusion() { return conclusion; }
    public void setConclusion(String conclusion) { this.conclusion = conclusion; }

    public List<String> getLegalBasis() { return legalBasis; }
    public void setLegalBasis(List<String> legalBasis) { this.legalBasis = legalBasis; }
    
    /**
     * 问题信息
     */
    public static class IssueInfo {
        /** 问题编号 */
        private String code;
        /** 问题描述 */
        private String description;
        /** 问题类型 */
        private String type;
        /** 严重程度: CRITICAL/HIGH/MEDIUM/LOW */
        private String severity;
        /** 涉及法规 */
        private String relatedRegulations;
        /** 整改建议 */
        private String remediation;

        // Getters and Setters
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }

        public String getRelatedRegulations() { return relatedRegulations; }
        public void setRelatedRegulations(String relatedRegulations) { this.relatedRegulations = relatedRegulations; }

        public String getRemediation() { return remediation; }
        public void setRemediation(String remediation) { this.remediation = remediation; }
    }
    
    /**
     * 建议信息
     */
    public static class SuggestionInfo {
        /** 建议编号 */
        private String code;
        /** 建议内容 */
        private String content;
        /** 优先级: HIGH/MEDIUM/LOW */
        private String priority;
        /** 实施难度 */
        private String difficulty;
        /** 预计工作量 */
        private String estimatedEffort;

        // Getters and Setters
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }

        public String getDifficulty() { return difficulty; }
        public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

        public String getEstimatedEffort() { return estimatedEffort; }
        public void setEstimatedEffort(String estimatedEffort) { this.estimatedEffort = estimatedEffort; }
    }
}
