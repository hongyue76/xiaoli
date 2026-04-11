package com.xiaoli.legal.analysis.model.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * 案件分析请求
 */
public class CaseAnalysisRequest {

    @NotBlank(message = "案件标题不能为空")
    private String caseTitle;
    @NotBlank(message = "案件类型不能为空")
    private String caseType;
    private String ourRole;
    private String opponent;
    private String claims;
    private String facts;
    private List<Evidence> evidences;
    private String analysisType = "ALL";

    // Getters
    public String getCaseTitle() { return caseTitle; }
    public String getCaseType() { return caseType; }
    public String getOurRole() { return ourRole; }
    public String getOpponent() { return opponent; }
    public String getClaims() { return claims; }
    public String getFacts() { return facts; }
    public List<Evidence> getEvidences() { return evidences; }
    public String getAnalysisType() { return analysisType; }

    // Setters
    public void setCaseTitle(String caseTitle) { this.caseTitle = caseTitle; }
    public void setCaseType(String caseType) { this.caseType = caseType; }
    public void setOurRole(String ourRole) { this.ourRole = ourRole; }
    public void setOpponent(String opponent) { this.opponent = opponent; }
    public void setClaims(String claims) { this.claims = claims; }
    public void setFacts(String facts) { this.facts = facts; }
    public void setEvidences(List<Evidence> evidences) { this.evidences = evidences; }
    public void setAnalysisType(String analysisType) { this.analysisType = analysisType; }

    public static class Evidence {
        private String name;
        private String type;
        private String content;
        private String purpose;

        // Getters
        public String getName() { return name; }
        public String getType() { return type; }
        public String getContent() { return content; }
        public String getPurpose() { return purpose; }

        // Setters
        public void setName(String name) { this.name = name; }
        public void setType(String type) { this.type = type; }
        public void setContent(String content) { this.content = content; }
        public void setPurpose(String purpose) { this.purpose = purpose; }
    }
}
