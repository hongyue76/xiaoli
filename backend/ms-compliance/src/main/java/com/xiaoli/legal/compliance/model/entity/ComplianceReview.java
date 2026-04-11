package com.xiaoli.legal.compliance.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 合规审查实体
 */
@Entity
@Table(name = "compliance_review")
public class ComplianceReview {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 企业ID */
    @Column(name = "company_id")
    private Long companyId;
    
    /** 审查类型: INTERNAL_CONTROLS(内控)/LEGAL(法律)/FINANCIAL(财务)/DATA(数据安全)/LABOR(劳动用工) */
    @Column(name = "review_type", length = 30)
    private String reviewType;
    
    /** 审查标题 */
    @Column(name = "title", length = 200)
    private String title;
    
    /** 审查范围 */
    @Column(name = "scope", columnDefinition = "TEXT")
    private String scope;
    
    /** 审查结果 */
    @Column(name = "result", columnDefinition = "TEXT")
    private String result;
    
    /** 风险等级 */
    @Column(name = "risk_level", length = 20)
    private String riskLevel;
    
    /** 风险评分 */
    @Column(name = "risk_score")
    private Integer riskScore;
    
    /** 发现问题数 */
    @Column(name = "issue_count")
    private Integer issueCount;
    
    /** 严重问题数 */
    @Column(name = "serious_issue_count")
    private Integer seriousIssueCount;
    
    /** 建议数量 */
    @Column(name = "suggestion_count")
    private Integer suggestionCount;
    
    /** 问题清单(JSON) */
    @Column(name = "issues", columnDefinition = "TEXT")
    private String issues;
    
    /** 建议清单(JSON) */
    @Column(name = "suggestions", columnDefinition = "TEXT")
    private String suggestions;
    
    /** 审查状态: PENDING/IN_PROGRESS/COMPLETED/ARCHIVED */
    @Column(name = "status", length = 20)
    private String status;
    
    /** 审查人 */
    @Column(name = "reviewer", length = 50)
    private String reviewer;
    
    /** 审查开始时间 */
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    /** 审查结束时间 */
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    /** 创建时间 */
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    
    public String getReviewType() { return reviewType; }
    public void setReviewType(String reviewType) { this.reviewType = reviewType; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    
    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }
    
    public Integer getIssueCount() { return issueCount; }
    public void setIssueCount(Integer issueCount) { this.issueCount = issueCount; }
    
    public Integer getSeriousIssueCount() { return seriousIssueCount; }
    public void setSeriousIssueCount(Integer seriousIssueCount) { this.seriousIssueCount = seriousIssueCount; }
    
    public Integer getSuggestionCount() { return suggestionCount; }
    public void setSuggestionCount(Integer suggestionCount) { this.suggestionCount = suggestionCount; }
    
    public String getIssues() { return issues; }
    public void setIssues(String issues) { this.issues = issues; }
    
    public String getSuggestions() { return suggestions; }
    public void setSuggestions(String suggestions) { this.suggestions = suggestions; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getReviewer() { return reviewer; }
    public void setReviewer(String reviewer) { this.reviewer = reviewer; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
