package com.xiaoli.legal.ms.legalcase.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 法律案例
 */
@TableName("legal_case")
public class LegalCase {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String caseNo;
    private String caseType;
    private String cause;
    private String court;
    private String judge;
    private LocalDate judgmentDate;
    private String caseStatus;
    private String parties;
    private String summary;
    private String disputeFocus;
    private String rulingIdea;
    private String judgmentResult;
    private String legalBasis;
    private String content;
    private String tags;
    private String vectorId;
    private Integer viewCount;
    private String source;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getCaseNo() { return caseNo; }
    public String getCaseType() { return caseType; }
    public String getCause() { return cause; }
    public String getCourt() { return court; }
    public String getJudge() { return judge; }
    public LocalDate getJudgmentDate() { return judgmentDate; }
    public String getCaseStatus() { return caseStatus; }
    public String getParties() { return parties; }
    public String getSummary() { return summary; }
    public String getDisputeFocus() { return disputeFocus; }
    public String getRulingIdea() { return rulingIdea; }
    public String getJudgmentResult() { return judgmentResult; }
    public String getLegalBasis() { return legalBasis; }
    public String getContent() { return content; }
    public String getTags() { return tags; }
    public String getVectorId() { return vectorId; }
    public Integer getViewCount() { return viewCount; }
    public String getSource() { return source; }
    public LocalDateTime getCreateTime() { return createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setCaseNo(String caseNo) { this.caseNo = caseNo; }
    public void setCaseType(String caseType) { this.caseType = caseType; }
    public void setCause(String cause) { this.cause = cause; }
    public void setCourt(String court) { this.court = court; }
    public void setJudge(String judge) { this.judge = judge; }
    public void setJudgmentDate(LocalDate judgmentDate) { this.judgmentDate = judgmentDate; }
    public void setCaseStatus(String caseStatus) { this.caseStatus = caseStatus; }
    public void setParties(String parties) { this.parties = parties; }
    public void setSummary(String summary) { this.summary = summary; }
    public void setDisputeFocus(String disputeFocus) { this.disputeFocus = disputeFocus; }
    public void setRulingIdea(String rulingIdea) { this.rulingIdea = rulingIdea; }
    public void setJudgmentResult(String judgmentResult) { this.judgmentResult = judgmentResult; }
    public void setLegalBasis(String legalBasis) { this.legalBasis = legalBasis; }
    public void setContent(String content) { this.content = content; }
    public void setTags(String tags) { this.tags = tags; }
    public void setVectorId(String vectorId) { this.vectorId = vectorId; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    public void setSource(String source) { this.source = source; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
