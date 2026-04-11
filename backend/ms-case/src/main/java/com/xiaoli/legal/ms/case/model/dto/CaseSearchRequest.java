package com.xiaoli.legal.ms.legalcase.model.dto;

/**
 * 案例检索请求
 */
public class CaseSearchRequest {

    private String keyword;
    private String caseType;
    private String court;
    private Integer year;
    private String caseStatus;
    private String cause;
    private String judge;
    private String sortBy = "RELEVANCE";
    private Long current = 1L;
    private Long size = 10L;
    private Boolean semantic = true;

    // Getters
    public String getKeyword() { return keyword; }
    public String getCaseType() { return caseType; }
    public String getCourt() { return court; }
    public Integer getYear() { return year; }
    public String getCaseStatus() { return caseStatus; }
    public String getCause() { return cause; }
    public String getJudge() { return judge; }
    public String getSortBy() { return sortBy; }
    public Long getCurrent() { return current; }
    public Long getSize() { return size; }
    public Boolean getSemantic() { return semantic; }

    // Setters
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public void setCaseType(String caseType) { this.caseType = caseType; }
    public void setCourt(String court) { this.court = court; }
    public void setYear(Integer year) { this.year = year; }
    public void setCaseStatus(String caseStatus) { this.caseStatus = caseStatus; }
    public void setCause(String cause) { this.cause = cause; }
    public void setJudge(String judge) { this.judge = judge; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    public void setCurrent(Long current) { this.current = current; }
    public void setSize(Long size) { this.size = size; }
    public void setSemantic(Boolean semantic) { this.semantic = semantic; }
}
