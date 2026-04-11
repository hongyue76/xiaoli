package com.xiaoli.legal.decision.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 案件决策分析实体
 */
@Entity
@Table(name = "case_decision")
public class CaseDecision {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 案件ID */
    @Column(name = "case_id")
    private Long caseId;
    
    /** 案件类型 */
    @Column(name = "case_type", length = 50)
    private String caseType;
    
    /** 案件描述 */
    @Column(name = "case_description", columnDefinition = "TEXT")
    private String caseDescription;
    
    /** 量刑建议 */
    @Column(name = "sentencing_suggestion", columnDefinition = "TEXT")
    private String sentencingSuggestion;
    
    /** 刑期范围-最小月数 */
    @Column(name = "sentence_min_months")
    private Integer sentenceMinMonths;
    
    /** 刑期范围-最大月数 */
    @Column(name = "sentence_max_months")
    private Integer sentenceMaxMonths;
    
    /** 缓刑建议 */
    @Column(name = "probation_suggestion")
    private Boolean probationSuggested;
    
    /** 罚金建议 */
    @Column(name = "fine_suggestion", length = 200)
    private String fineSuggestion;
    
    /** 审判预测结果 */
    @Column(name = "trial_prediction", length = 200)
    private String trialPrediction;
    
    /** 胜诉概率 */
    @Column(name = "win_probability")
    private Double winProbability;
    
    /** 关键因素分析 */
    @Column(name = "key_factors", columnDefinition = "TEXT")
    private String keyFactors;
    
    /** 风险提示 */
    @Column(name = "risk_warnings", columnDefinition = "TEXT")
    private String riskWarnings;
    
    /** 决策建议 */
    @Column(name = "decision_suggestions", columnDefinition = "TEXT")
    private String decisionSuggestions;
    
    /** 置信度 */
    @Column(name = "confidence_level")
    private Double confidenceLevel;
    
    /** 参考案例IDs */
    @Column(name = "reference_case_ids", length = 500)
    private String referenceCaseIds;
    
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
    
    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }
    
    public String getCaseType() { return caseType; }
    public void setCaseType(String caseType) { this.caseType = caseType; }
    
    public String getCaseDescription() { return caseDescription; }
    public void setCaseDescription(String caseDescription) { this.caseDescription = caseDescription; }
    
    public String getSentencingSuggestion() { return sentencingSuggestion; }
    public void setSentencingSuggestion(String sentencingSuggestion) { this.sentencingSuggestion = sentencingSuggestion; }
    
    public Integer getSentenceMinMonths() { return sentenceMinMonths; }
    public void setSentenceMinMonths(Integer sentenceMinMonths) { this.sentenceMinMonths = sentenceMinMonths; }
    
    public Integer getSentenceMaxMonths() { return sentenceMaxMonths; }
    public void setSentenceMaxMonths(Integer sentenceMaxMonths) { this.sentenceMaxMonths = sentenceMaxMonths; }
    
    public Boolean getProbationSuggested() { return probationSuggested; }
    public void setProbationSuggested(Boolean probationSuggested) { this.probationSuggested = probationSuggested; }
    
    public String getFineSuggestion() { return fineSuggestion; }
    public void setFineSuggestion(String fineSuggestion) { this.fineSuggestion = fineSuggestion; }
    
    public String getTrialPrediction() { return trialPrediction; }
    public void setTrialPrediction(String trialPrediction) { this.trialPrediction = trialPrediction; }
    
    public Double getWinProbability() { return winProbability; }
    public void setWinProbability(Double winProbability) { this.winProbability = winProbability; }
    
    public String getKeyFactors() { return keyFactors; }
    public void setKeyFactors(String keyFactors) { this.keyFactors = keyFactors; }
    
    public String getRiskWarnings() { return riskWarnings; }
    public void setRiskWarnings(String riskWarnings) { this.riskWarnings = riskWarnings; }
    
    public String getDecisionSuggestions() { return decisionSuggestions; }
    public void setDecisionSuggestions(String decisionSuggestions) { this.decisionSuggestions = decisionSuggestions; }
    
    public Double getConfidenceLevel() { return confidenceLevel; }
    public void setConfidenceLevel(Double confidenceLevel) { this.confidenceLevel = confidenceLevel; }
    
    public String getReferenceCaseIds() { return referenceCaseIds; }
    public void setReferenceCaseIds(String referenceCaseIds) { this.referenceCaseIds = referenceCaseIds; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
