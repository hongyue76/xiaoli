package com.xiaoli.legal.decision.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 裁判文书参考实体
 */
@Entity
@Table(name = "judgment_reference")
public class JudgmentReference {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 案件编号 */
    @Column(name = "case_number", length = 100)
    private String caseNumber;
    
    /** 案件类型 */
    @Column(name = "case_type", length = 50)
    private String caseType;
    
    /** 法院级别 */
    @Column(name = "court_level", length = 20)
    private String courtLevel;
    
    /** 判决结果 */
    @Column(name = "judgment_result", length = 200)
    private String judgmentResult;
    
    /** 刑期/处罚 */
    @Column(name = "sentence", length = 200)
    private String sentence;
    
    /** 判决日期 */
    @Column(name = "judgment_date")
    private LocalDateTime judgmentDate;
    
    /** 关键事实 */
    @Column(name = "key_facts", columnDefinition = "TEXT")
    private String keyFacts;
    
    /** 判决理由 */
    @Column(name = "judgment_reason", columnDefinition = "TEXT")
    private String judgmentReason;
    
    /** 法律依据 */
    @Column(name = "legal_basis", columnDefinition = "TEXT")
    private String legalBasis;
    
    /** 裁判要点 */
    @Column(name = "key_points", columnDefinition = "TEXT")
    private String keyPoints;
    
    /** 相似度评分 */
    @Column(name = "similarity_score")
    private Double similarityScore;
    
    /** 创建时间 */
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCaseNumber() { return caseNumber; }
    public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }
    
    public String getCaseType() { return caseType; }
    public void setCaseType(String caseType) { this.caseType = caseType; }
    
    public String getCourtLevel() { return courtLevel; }
    public void setCourtLevel(String courtLevel) { this.courtLevel = courtLevel; }
    
    public String getJudgmentResult() { return judgmentResult; }
    public void setJudgmentResult(String judgmentResult) { this.judgmentResult = judgmentResult; }
    
    public String getSentence() { return sentence; }
    public void setSentence(String sentence) { this.sentence = sentence; }
    
    public LocalDateTime getJudgmentDate() { return judgmentDate; }
    public void setJudgmentDate(LocalDateTime judgmentDate) { this.judgmentDate = judgmentDate; }
    
    public String getKeyFacts() { return keyFacts; }
    public void setKeyFacts(String keyFacts) { this.keyFacts = keyFacts; }
    
    public String getJudgmentReason() { return judgmentReason; }
    public void setJudgmentReason(String judgmentReason) { this.judgmentReason = judgmentReason; }
    
    public String getLegalBasis() { return legalBasis; }
    public void setLegalBasis(String legalBasis) { this.legalBasis = legalBasis; }
    
    public String getKeyPoints() { return keyPoints; }
    public void setKeyPoints(String keyPoints) { this.keyPoints = keyPoints; }
    
    public Double getSimilarityScore() { return similarityScore; }
    public void setSimilarityScore(Double similarityScore) { this.similarityScore = similarityScore; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
