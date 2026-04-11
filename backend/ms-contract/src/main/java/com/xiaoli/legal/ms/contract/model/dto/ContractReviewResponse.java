package com.xiaoli.legal.ms.contract.model.dto;

import java.util.List;

/**
 * 合同审查响应
 */
public class ContractReviewResponse {

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 合同名称
     */
    private String name;

    /**
     * 审查状态
     */
    private String reviewStatus;

    /**
     * 风险等级
     */
    private String riskLevel;

    /**
     * 风险评分 (0-100)
     */
    private Integer riskScore;

    /**
     * 问题数量统计
     */
    private IssueCount issueCount;

    /**
     * 问题列表
     */
    private List<IssueDetail> issues;

    /**
     * 审查维度评分
     */
    private DimensionScore dimensionScore;

    /**
     * AI总结建议
     */
    private String aiSummary;

    // Getters and Setters
    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getReviewStatus() { return reviewStatus; }
    public void setReviewStatus(String reviewStatus) { this.reviewStatus = reviewStatus; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }

    public IssueCount getIssueCount() { return issueCount; }
    public void setIssueCount(IssueCount issueCount) { this.issueCount = issueCount; }

    public List<IssueDetail> getIssues() { return issues; }
    public void setIssues(List<IssueDetail> issues) { this.issues = issues; }

    public DimensionScore getDimensionScore() { return dimensionScore; }
    public void setDimensionScore(DimensionScore dimensionScore) { this.dimensionScore = dimensionScore; }

    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }

    /**
     * 问题数量统计
     */
    public static class IssueCount {
        private Integer total;
        private Integer high;
        private Integer medium;
        private Integer low;

        public IssueCount() {}

        public IssueCount(Integer total, Integer high, Integer medium, Integer low) {
            this.total = total;
            this.high = high;
            this.medium = medium;
            this.low = low;
        }

        // Getters and Setters
        public Integer getTotal() { return total; }
        public void setTotal(Integer total) { this.total = total; }

        public Integer getHigh() { return high; }
        public void setHigh(Integer high) { this.high = high; }

        public Integer getMedium() { return medium; }
        public void setMedium(Integer medium) { this.medium = medium; }

        public Integer getLow() { return low; }
        public void setLow(Integer low) { this.low = low; }

        // Builder
        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private Integer total;
            private Integer high;
            private Integer medium;
            private Integer low;

            public Builder total(Integer total) { this.total = total; return this; }
            public Builder high(Integer high) { this.high = high; return this; }
            public Builder medium(Integer medium) { this.medium = medium; return this; }
            public Builder low(Integer low) { this.low = low; return this; }

            public IssueCount build() {
                return new IssueCount(total, high, medium, low);
            }
        }
    }

    /**
     * 问题详情
     */
    public static class IssueDetail {
        private Long id;
        private String title;
        private String location;
        private String issueType;
        private String severity;
        private String description;
        private String legalBasis;
        private String suggestion;

        public IssueDetail() {}

        public IssueDetail(Long id, String title, String location, String issueType, String severity,
                          String description, String legalBasis, String suggestion) {
            this.id = id;
            this.title = title;
            this.location = location;
            this.issueType = issueType;
            this.severity = severity;
            this.description = description;
            this.legalBasis = legalBasis;
            this.suggestion = suggestion;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

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

        // Builder
        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private Long id;
            private String title;
            private String location;
            private String issueType;
            private String severity;
            private String description;
            private String legalBasis;
            private String suggestion;

            public Builder id(Long id) { this.id = id; return this; }
            public Builder title(String title) { this.title = title; return this; }
            public Builder location(String location) { this.location = location; return this; }
            public Builder issueType(String issueType) { this.issueType = issueType; return this; }
            public Builder severity(String severity) { this.severity = severity; return this; }
            public Builder description(String description) { this.description = description; return this; }
            public Builder legalBasis(String legalBasis) { this.legalBasis = legalBasis; return this; }
            public Builder suggestion(String suggestion) { this.suggestion = suggestion; return this; }

            public IssueDetail build() {
                return new IssueDetail(id, title, location, issueType, severity, description, legalBasis, suggestion);
            }
        }
    }

    /**
     * 审查维度评分
     */
    public static class DimensionScore {
        /**
         * 完整性评分
         */
        private Integer completeness;

        /**
         * 合法性评分
         */
        private Integer legality;

        /**
         * 公平性评分
         */
        private Integer fairness;

        /**
         * 风险性评分
         */
        private Integer risk;

        /**
         * 可执行性评分
         */
        private Integer executability;

        public DimensionScore() {}

        public DimensionScore(Integer completeness, Integer legality, Integer fairness, Integer risk, Integer executability) {
            this.completeness = completeness;
            this.legality = legality;
            this.fairness = fairness;
            this.risk = risk;
            this.executability = executability;
        }

        // Getters and Setters
        public Integer getCompleteness() { return completeness; }
        public void setCompleteness(Integer completeness) { this.completeness = completeness; }

        public Integer getLegality() { return legality; }
        public void setLegality(Integer legality) { this.legality = legality; }

        public Integer getFairness() { return fairness; }
        public void setFairness(Integer fairness) { this.fairness = fairness; }

        public Integer getRisk() { return risk; }
        public void setRisk(Integer risk) { this.risk = risk; }

        public Integer getExecutability() { return executability; }
        public void setExecutability(Integer executability) { this.executability = executability; }

        // Builder
        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private Integer completeness;
            private Integer legality;
            private Integer fairness;
            private Integer risk;
            private Integer executability;

            public Builder completeness(Integer completeness) { this.completeness = completeness; return this; }
            public Builder legality(Integer legality) { this.legality = legality; return this; }
            public Builder fairness(Integer fairness) { this.fairness = fairness; return this; }
            public Builder risk(Integer risk) { this.risk = risk; return this; }
            public Builder executability(Integer executability) { this.executability = executability; return this; }

            public DimensionScore build() {
                return new DimensionScore(completeness, legality, fairness, risk, executability);
            }
        }
    }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long contractId;
        private String name;
        private String reviewStatus;
        private String riskLevel;
        private Integer riskScore;
        private IssueCount issueCount;
        private List<IssueDetail> issues;
        private DimensionScore dimensionScore;
        private String aiSummary;

        public Builder contractId(Long contractId) { this.contractId = contractId; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder reviewStatus(String reviewStatus) { this.reviewStatus = reviewStatus; return this; }
        public Builder riskLevel(String riskLevel) { this.riskLevel = riskLevel; return this; }
        public Builder riskScore(Integer riskScore) { this.riskScore = riskScore; return this; }
        public Builder issueCount(IssueCount issueCount) { this.issueCount = issueCount; return this; }
        public Builder issues(List<IssueDetail> issues) { this.issues = issues; return this; }
        public Builder dimensionScore(DimensionScore dimensionScore) { this.dimensionScore = dimensionScore; return this; }
        public Builder aiSummary(String aiSummary) { this.aiSummary = aiSummary; return this; }

        public ContractReviewResponse build() {
            ContractReviewResponse response = new ContractReviewResponse();
            response.setContractId(contractId);
            response.setName(name);
            response.setReviewStatus(reviewStatus);
            response.setRiskLevel(riskLevel);
            response.setRiskScore(riskScore);
            response.setIssueCount(issueCount);
            response.setIssues(issues);
            response.setDimensionScore(dimensionScore);
            response.setAiSummary(aiSummary);
            return response;
        }
    }
}
