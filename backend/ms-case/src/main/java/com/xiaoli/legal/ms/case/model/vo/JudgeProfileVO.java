package com.xiaoli.legal.ms.legalcase.model.vo;

import java.util.List;
import java.util.Map;

/**
 * 法官画像VO
 */
public class JudgeProfileVO {

    private String judgeName;
    private String court;
    private Integer totalCaseCount;
    private Map<String, Integer> caseTypeDistribution;
    private Map<String, Integer> caseStatusDistribution;
    private String judgingStyle;
    private String tendencyAnalysis;
    private String winLoseRatio;
    private List<String> commonLegalBasis;
    private List<CaseSearchResultVO> typicalCases;
    private String strategyAdvice;
    private String precautions;

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final JudgeProfileVO vo = new JudgeProfileVO();

        public Builder judgeName(String judgeName) {
            vo.judgeName = judgeName;
            return this;
        }

        public Builder court(String court) {
            vo.court = court;
            return this;
        }

        public Builder totalCaseCount(Integer totalCaseCount) {
            vo.totalCaseCount = totalCaseCount;
            return this;
        }

        public Builder caseTypeDistribution(Map<String, Integer> caseTypeDistribution) {
            vo.caseTypeDistribution = caseTypeDistribution;
            return this;
        }

        public Builder caseStatusDistribution(Map<String, Integer> caseStatusDistribution) {
            vo.caseStatusDistribution = caseStatusDistribution;
            return this;
        }

        public Builder judgingStyle(String judgingStyle) {
            vo.judgingStyle = judgingStyle;
            return this;
        }

        public Builder tendencyAnalysis(String tendencyAnalysis) {
            vo.tendencyAnalysis = tendencyAnalysis;
            return this;
        }

        public Builder winLoseRatio(String winLoseRatio) {
            vo.winLoseRatio = winLoseRatio;
            return this;
        }

        public Builder commonLegalBasis(List<String> commonLegalBasis) {
            vo.commonLegalBasis = commonLegalBasis;
            return this;
        }

        public Builder typicalCases(List<CaseSearchResultVO> typicalCases) {
            vo.typicalCases = typicalCases;
            return this;
        }

        public Builder strategyAdvice(String strategyAdvice) {
            vo.strategyAdvice = strategyAdvice;
            return this;
        }

        public Builder precautions(String precautions) {
            vo.precautions = precautions;
            return this;
        }

        public JudgeProfileVO build() {
            return vo;
        }
    }

    // Getters
    public String getJudgeName() { return judgeName; }
    public String getCourt() { return court; }
    public Integer getTotalCaseCount() { return totalCaseCount; }
    public Map<String, Integer> getCaseTypeDistribution() { return caseTypeDistribution; }
    public Map<String, Integer> getCaseStatusDistribution() { return caseStatusDistribution; }
    public String getJudgingStyle() { return judgingStyle; }
    public String getTendencyAnalysis() { return tendencyAnalysis; }
    public String getWinLoseRatio() { return winLoseRatio; }
    public List<String> getCommonLegalBasis() { return commonLegalBasis; }
    public List<CaseSearchResultVO> getTypicalCases() { return typicalCases; }
    public String getStrategyAdvice() { return strategyAdvice; }
    public String getPrecautions() { return precautions; }

    // Setters
    public void setJudgeName(String judgeName) { this.judgeName = judgeName; }
    public void setCourt(String court) { this.court = court; }
    public void setTotalCaseCount(Integer totalCaseCount) { this.totalCaseCount = totalCaseCount; }
    public void setCaseTypeDistribution(Map<String, Integer> caseTypeDistribution) { this.caseTypeDistribution = caseTypeDistribution; }
    public void setCaseStatusDistribution(Map<String, Integer> caseStatusDistribution) { this.caseStatusDistribution = caseStatusDistribution; }
    public void setJudgingStyle(String judgingStyle) { this.judgingStyle = judgingStyle; }
    public void setTendencyAnalysis(String tendencyAnalysis) { this.tendencyAnalysis = tendencyAnalysis; }
    public void setWinLoseRatio(String winLoseRatio) { this.winLoseRatio = winLoseRatio; }
    public void setCommonLegalBasis(List<String> commonLegalBasis) { this.commonLegalBasis = commonLegalBasis; }
    public void setTypicalCases(List<CaseSearchResultVO> typicalCases) { this.typicalCases = typicalCases; }
    public void setStrategyAdvice(String strategyAdvice) { this.strategyAdvice = strategyAdvice; }
    public void setPrecautions(String precautions) { this.precautions = precautions; }
}
