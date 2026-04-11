package com.xiaoli.legal.decision.model.dto;

import java.util.List;
import java.util.Map;

/**
 * 司法决策响应DTO
 */
public class DecisionResponse {

    private Long decisionId;
    private String decisionType;
    private SentencingInfo sentencing;
    private TrialPredictionInfo trialPrediction;
    private JudgmentSuggestionInfo judgmentSuggestion;
    private RiskAssessment riskAssessment;
    private List<ReferenceCaseInfo> referenceCases;
    private Double confidenceLevel;
    private List<String> keyFactors;
    private List<String> suggestions;
    private List<String> legalBasis;
    private List<String> riskWarnings;

    // Getters
    public Long getDecisionId() { return decisionId; }
    public String getDecisionType() { return decisionType; }
    public SentencingInfo getSentencing() { return sentencing; }
    public TrialPredictionInfo getTrialPrediction() { return trialPrediction; }
    public JudgmentSuggestionInfo getJudgmentSuggestion() { return judgmentSuggestion; }
    public RiskAssessment getRiskAssessment() { return riskAssessment; }
    public List<ReferenceCaseInfo> getReferenceCases() { return referenceCases; }
    public Double getConfidenceLevel() { return confidenceLevel; }
    public List<String> getKeyFactors() { return keyFactors; }
    public List<String> getSuggestions() { return suggestions; }
    public List<String> getLegalBasis() { return legalBasis; }
    public List<String> getRiskWarnings() { return riskWarnings; }

    // Setters
    public void setDecisionId(Long decisionId) { this.decisionId = decisionId; }
    public void setDecisionType(String decisionType) { this.decisionType = decisionType; }
    public void setSentencing(SentencingInfo sentencing) { this.sentencing = sentencing; }
    public void setTrialPrediction(TrialPredictionInfo trialPrediction) { this.trialPrediction = trialPrediction; }
    public void setJudgmentSuggestion(JudgmentSuggestionInfo judgmentSuggestion) { this.judgmentSuggestion = judgmentSuggestion; }
    public void setRiskAssessment(RiskAssessment riskAssessment) { this.riskAssessment = riskAssessment; }
    public void setReferenceCases(List<ReferenceCaseInfo> referenceCases) { this.referenceCases = referenceCases; }
    public void setConfidenceLevel(Double confidenceLevel) { this.confidenceLevel = confidenceLevel; }
    public void setKeyFactors(List<String> keyFactors) { this.keyFactors = keyFactors; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
    public void setLegalBasis(List<String> legalBasis) { this.legalBasis = legalBasis; }
    public void setRiskWarnings(List<String> riskWarnings) { this.riskWarnings = riskWarnings; }

    public static class SentencingInfo {
        private String suggestedSentence;
        private String sentenceRange;
        private Integer minMonths;
        private Integer maxMonths;
        private Boolean probationRecommended;
        private String probationPeriod;
        private String fineSuggestion;
        private String reasoning;

        // Getters
        public String getSuggestedSentence() { return suggestedSentence; }
        public String getSentenceRange() { return sentenceRange; }
        public Integer getMinMonths() { return minMonths; }
        public Integer getMaxMonths() { return maxMonths; }
        public Boolean getProbationRecommended() { return probationRecommended; }
        public String getProbationPeriod() { return probationPeriod; }
        public String getFineSuggestion() { return fineSuggestion; }
        public String getReasoning() { return reasoning; }

        // Setters
        public void setSuggestedSentence(String suggestedSentence) { this.suggestedSentence = suggestedSentence; }
        public void setSentenceRange(String sentenceRange) { this.sentenceRange = sentenceRange; }
        public void setMinMonths(Integer minMonths) { this.minMonths = minMonths; }
        public void setMaxMonths(Integer maxMonths) { this.maxMonths = maxMonths; }
        public void setProbationRecommended(Boolean probationRecommended) { this.probationRecommended = probationRecommended; }
        public void setProbationPeriod(String probationPeriod) { this.probationPeriod = probationPeriod; }
        public void setFineSuggestion(String fineSuggestion) { this.fineSuggestion = fineSuggestion; }
        public void setReasoning(String reasoning) { this.reasoning = reasoning; }

        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private String suggestedSentence;
            private String sentenceRange;
            private Integer minMonths;
            private Integer maxMonths;
            private Boolean probationRecommended;
            private String probationPeriod;
            private String fineSuggestion;
            private String reasoning;

            public Builder suggestedSentence(String v) { this.suggestedSentence = v; return this; }
            public Builder sentenceRange(String v) { this.sentenceRange = v; return this; }
            public Builder minMonths(Integer v) { this.minMonths = v; return this; }
            public Builder maxMonths(Integer v) { this.maxMonths = v; return this; }
            public Builder probationRecommended(Boolean v) { this.probationRecommended = v; return this; }
            public Builder probationPeriod(String v) { this.probationPeriod = v; return this; }
            public Builder fineSuggestion(String v) { this.fineSuggestion = v; return this; }
            public Builder reasoning(String v) { this.reasoning = v; return this; }
            public SentencingInfo build() {
                SentencingInfo info = new SentencingInfo();
                info.suggestedSentence = this.suggestedSentence;
                info.sentenceRange = this.sentenceRange;
                info.minMonths = this.minMonths;
                info.maxMonths = this.maxMonths;
                info.probationRecommended = this.probationRecommended;
                info.probationPeriod = this.probationPeriod;
                info.fineSuggestion = this.fineSuggestion;
                info.reasoning = this.reasoning;
                return info;
            }
        }
    }

    public static class TrialPredictionInfo {
        private String predictedResult;
        private Double probability;
        private List<String> favorableFactors;
        private List<String> unfavorableFactors;
        private String recommendedStrategy;

        // Getters
        public String getPredictedResult() { return predictedResult; }
        public Double getProbability() { return probability; }
        public List<String> getFavorableFactors() { return favorableFactors; }
        public List<String> getUnfavorableFactors() { return unfavorableFactors; }
        public String getRecommendedStrategy() { return recommendedStrategy; }

        // Setters
        public void setPredictedResult(String predictedResult) { this.predictedResult = predictedResult; }
        public void setProbability(Double probability) { this.probability = probability; }
        public void setFavorableFactors(List<String> favorableFactors) { this.favorableFactors = favorableFactors; }
        public void setUnfavorableFactors(List<String> unfavorableFactors) { this.unfavorableFactors = unfavorableFactors; }
        public void setRecommendedStrategy(String recommendedStrategy) { this.recommendedStrategy = recommendedStrategy; }

        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private String predictedResult;
            private Double probability;
            private List<String> favorableFactors;
            private List<String> unfavorableFactors;
            private String recommendedStrategy;

            public Builder predictedResult(String v) { this.predictedResult = v; return this; }
            public Builder probability(Double v) { this.probability = v; return this; }
            public Builder favorableFactors(List<String> v) { this.favorableFactors = v; return this; }
            public Builder unfavorableFactors(List<String> v) { this.unfavorableFactors = v; return this; }
            public Builder recommendedStrategy(String v) { this.recommendedStrategy = v; return this; }
            public TrialPredictionInfo build() {
                TrialPredictionInfo info = new TrialPredictionInfo();
                info.predictedResult = this.predictedResult;
                info.probability = this.probability;
                info.favorableFactors = this.favorableFactors;
                info.unfavorableFactors = this.unfavorableFactors;
                info.recommendedStrategy = this.recommendedStrategy;
                return info;
            }
        }
    }

    public static class JudgmentSuggestionInfo {
        private String suggestedJudgment;
        private String reasoning;
        private String legalApplication;
        private List<String> keyPoints;

        // Getters
        public String getSuggestedJudgment() { return suggestedJudgment; }
        public String getReasoning() { return reasoning; }
        public String getLegalApplication() { return legalApplication; }
        public List<String> getKeyPoints() { return keyPoints; }

        // Setters
        public void setSuggestedJudgment(String suggestedJudgment) { this.suggestedJudgment = suggestedJudgment; }
        public void setReasoning(String reasoning) { this.reasoning = reasoning; }
        public void setLegalApplication(String legalApplication) { this.legalApplication = legalApplication; }
        public void setKeyPoints(List<String> keyPoints) { this.keyPoints = keyPoints; }

        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private String suggestedJudgment;
            private String reasoning;
            private String legalApplication;
            private List<String> keyPoints;

            public Builder suggestedJudgment(String v) { this.suggestedJudgment = v; return this; }
            public Builder reasoning(String v) { this.reasoning = v; return this; }
            public Builder legalApplication(String v) { this.legalApplication = v; return this; }
            public Builder keyPoints(List<String> v) { this.keyPoints = v; return this; }
            public JudgmentSuggestionInfo build() {
                JudgmentSuggestionInfo info = new JudgmentSuggestionInfo();
                info.suggestedJudgment = this.suggestedJudgment;
                info.reasoning = this.reasoning;
                info.legalApplication = this.legalApplication;
                info.keyPoints = this.keyPoints;
                return info;
            }
        }
    }

    public static class RiskAssessment {
        private String riskLevel;
        private List<String> riskDescriptions;
        private List<String> mitigationSuggestions;

        // Getters
        public String getRiskLevel() { return riskLevel; }
        public List<String> getRiskDescriptions() { return riskDescriptions; }
        public List<String> getMitigationSuggestions() { return mitigationSuggestions; }

        // Setters
        public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
        public void setRiskDescriptions(List<String> riskDescriptions) { this.riskDescriptions = riskDescriptions; }
        public void setMitigationSuggestions(List<String> mitigationSuggestions) { this.mitigationSuggestions = mitigationSuggestions; }

        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private String riskLevel;
            private List<String> riskDescriptions;
            private List<String> mitigationSuggestions;

            public Builder riskLevel(String v) { this.riskLevel = v; return this; }
            public Builder riskDescriptions(List<String> v) { this.riskDescriptions = v; return this; }
            public Builder mitigationSuggestions(List<String> v) { this.mitigationSuggestions = v; return this; }
            public RiskAssessment build() {
                RiskAssessment info = new RiskAssessment();
                info.riskLevel = this.riskLevel;
                info.riskDescriptions = this.riskDescriptions;
                info.mitigationSuggestions = this.mitigationSuggestions;
                return info;
            }
        }
    }

    public static class ReferenceCaseInfo {
        private Long caseId;
        private String caseNumber;
        private String caseType;
        private String judgmentResult;
        private Double similarity;
        private List<String> keyPoints;

        // Getters
        public Long getCaseId() { return caseId; }
        public String getCaseNumber() { return caseNumber; }
        public String getCaseType() { return caseType; }
        public String getJudgmentResult() { return judgmentResult; }
        public Double getSimilarity() { return similarity; }
        public List<String> getKeyPoints() { return keyPoints; }

        // Setters
        public void setCaseId(Long caseId) { this.caseId = caseId; }
        public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }
        public void setCaseType(String caseType) { this.caseType = caseType; }
        public void setJudgmentResult(String judgmentResult) { this.judgmentResult = judgmentResult; }
        public void setSimilarity(Double similarity) { this.similarity = similarity; }
        public void setKeyPoints(List<String> keyPoints) { this.keyPoints = keyPoints; }

        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private Long caseId;
            private String caseNumber;
            private String caseType;
            private String judgmentResult;
            private Double similarity;
            private List<String> keyPoints;

            public Builder caseId(Long v) { this.caseId = v; return this; }
            public Builder caseNumber(String v) { this.caseNumber = v; return this; }
            public Builder caseType(String v) { this.caseType = v; return this; }
            public Builder judgmentResult(String v) { this.judgmentResult = v; return this; }
            public Builder similarity(Double v) { this.similarity = v; return this; }
            public Builder keyPoints(List<String> v) { this.keyPoints = v; return this; }
            public ReferenceCaseInfo build() {
                ReferenceCaseInfo info = new ReferenceCaseInfo();
                info.caseId = this.caseId;
                info.caseNumber = this.caseNumber;
                info.caseType = this.caseType;
                info.judgmentResult = this.judgmentResult;
                info.similarity = this.similarity;
                info.keyPoints = this.keyPoints;
                return info;
            }
        }
    }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long decisionId;
        private String decisionType;
        private SentencingInfo sentencing;
        private TrialPredictionInfo trialPrediction;
        private JudgmentSuggestionInfo judgmentSuggestion;
        private RiskAssessment riskAssessment;
        private List<ReferenceCaseInfo> referenceCases;
        private Double confidenceLevel;
        private List<String> keyFactors;
        private List<String> suggestions;
        private List<String> legalBasis;
        private List<String> riskWarnings;

        public Builder decisionId(Long v) { this.decisionId = v; return this; }
        public Builder decisionType(String v) { this.decisionType = v; return this; }
        public Builder sentencing(SentencingInfo v) { this.sentencing = v; return this; }
        public Builder trialPrediction(TrialPredictionInfo v) { this.trialPrediction = v; return this; }
        public Builder judgmentSuggestion(JudgmentSuggestionInfo v) { this.judgmentSuggestion = v; return this; }
        public Builder riskAssessment(RiskAssessment v) { this.riskAssessment = v; return this; }
        public Builder referenceCases(List<ReferenceCaseInfo> v) { this.referenceCases = v; return this; }
        public Builder confidenceLevel(Double v) { this.confidenceLevel = v; return this; }
        public Builder keyFactors(List<String> v) { this.keyFactors = v; return this; }
        public Builder suggestions(List<String> v) { this.suggestions = v; return this; }
        public Builder legalBasis(List<String> v) { this.legalBasis = v; return this; }
        public Builder riskWarnings(List<String> v) { this.riskWarnings = v; return this; }
        public DecisionResponse build() {
            DecisionResponse resp = new DecisionResponse();
            resp.decisionId = this.decisionId;
            resp.decisionType = this.decisionType;
            resp.sentencing = this.sentencing;
            resp.trialPrediction = this.trialPrediction;
            resp.judgmentSuggestion = this.judgmentSuggestion;
            resp.riskAssessment = this.riskAssessment;
            resp.referenceCases = this.referenceCases;
            resp.confidenceLevel = this.confidenceLevel;
            resp.keyFactors = this.keyFactors;
            resp.suggestions = this.suggestions;
            resp.legalBasis = this.legalBasis;
            resp.riskWarnings = this.riskWarnings;
            return resp;
        }
    }
}
