package com.xiaoli.legal.ms.legalcase.model.vo;

import java.time.LocalDate;
import java.util.List;

/**
 * 案例检索结果VO
 */
public class CaseSearchResultVO {

    private Long id;
    private String title;
    private String caseNo;
    private String caseType;
    private String cause;
    private String court;
    private String judge;
    private LocalDate judgmentDate;
    private String caseStatus;
    private String summary;
    private String disputeFocus;
    private String rulingIdea;
    private String judgmentResult;
    private String legalBasis;
    private Double score;
    private List<String> tags;
    private Integer viewCount;
    private String source;

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
    public String getSummary() { return summary; }
    public String getDisputeFocus() { return disputeFocus; }
    public String getRulingIdea() { return rulingIdea; }
    public String getJudgmentResult() { return judgmentResult; }
    public String getLegalBasis() { return legalBasis; }
    public Double getScore() { return score; }
    public List<String> getTags() { return tags; }
    public Integer getViewCount() { return viewCount; }
    public String getSource() { return source; }

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
    public void setSummary(String summary) { this.summary = summary; }
    public void setDisputeFocus(String disputeFocus) { this.disputeFocus = disputeFocus; }
    public void setRulingIdea(String rulingIdea) { this.rulingIdea = rulingIdea; }
    public void setJudgmentResult(String judgmentResult) { this.judgmentResult = judgmentResult; }
    public void setLegalBasis(String legalBasis) { this.legalBasis = legalBasis; }
    public void setScore(Double score) { this.score = score; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    public void setSource(String source) { this.source = source; }
}
