package com.xiaoli.legal.compliance.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 合规风险实体
 */
@Entity
@Table(name = "compliance_risk")
public class ComplianceRisk {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 企业ID */
    @Column(name = "company_id")
    private Long companyId;
    
    /** 风险类型 */
    @Column(name = "risk_type", length = 50)
    private String riskType;
    
    /** 风险名称 */
    @Column(name = "risk_name", length = 200)
    private String riskName;
    
    /** 风险描述 */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /** 风险等级: HIGH/MEDIUM/LOW */
    @Column(name = "risk_level", length = 20)
    private String riskLevel;
    
    /** 风险评分 */
    @Column(name = "risk_score")
    private Integer riskScore;
    
    /** 影响范围 */
    @Column(name = "impact_scope", length = 100)
    private String impactScope;
    
    /** 潜在后果 */
    @Column(name = "potential_consequences", columnDefinition = "TEXT")
    private String potentialConsequences;
    
    /** 发生概率 */
    @Column(name = "probability")
    private Double probability;
    
    /** 建议措施 */
    @Column(name = "recommended_actions", columnDefinition = "TEXT")
    private String recommendedActions;
    
    /** 责任部门 */
    @Column(name = "responsible_dept", length = 50)
    private String responsibleDept;
    
    /** 责任人 */
    @Column(name = "responsible_person", length = 50)
    private String responsiblePerson;
    
    /** 整改期限 */
    @Column(name = "deadline")
    private LocalDateTime deadline;
    
    /** 整改状态: PENDING/IN_PROGRESS/COMPLETED/OVERDUE */
    @Column(name = "remediation_status", length = 20)
    private String remediationStatus;
    
    /** 备注 */
    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;
    
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
    
    public String getRiskType() { return riskType; }
    public void setRiskType(String riskType) { this.riskType = riskType; }
    
    public String getRiskName() { return riskName; }
    public void setRiskName(String riskName) { this.riskName = riskName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    
    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }
    
    public String getImpactScope() { return impactScope; }
    public void setImpactScope(String impactScope) { this.impactScope = impactScope; }
    
    public String getPotentialConsequences() { return potentialConsequences; }
    public void setPotentialConsequences(String potentialConsequences) { this.potentialConsequences = potentialConsequences; }
    
    public Double getProbability() { return probability; }
    public void setProbability(Double probability) { this.probability = probability; }
    
    public String getRecommendedActions() { return recommendedActions; }
    public void setRecommendedActions(String recommendedActions) { this.recommendedActions = recommendedActions; }
    
    public String getResponsibleDept() { return responsibleDept; }
    public void setResponsibleDept(String responsibleDept) { this.responsibleDept = responsibleDept; }
    
    public String getResponsiblePerson() { return responsiblePerson; }
    public void setResponsiblePerson(String responsiblePerson) { this.responsiblePerson = responsiblePerson; }
    
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    
    public String getRemediationStatus() { return remediationStatus; }
    public void setRemediationStatus(String remediationStatus) { this.remediationStatus = remediationStatus; }
    
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
