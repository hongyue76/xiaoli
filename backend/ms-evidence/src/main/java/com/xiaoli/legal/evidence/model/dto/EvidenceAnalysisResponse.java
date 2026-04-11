package com.xiaoli.legal.evidence.model.dto;

import java.util.List;

/**
 * 证据分析响应
 */
public class EvidenceAnalysisResponse {

    /**
     * 证据总数
     */
    private Integer totalCount;

    /**
     * 证据链完整性评估
     */
    private String chainIntegrity;

    /**
     * 整体证明力评分
     */
    private Integer overallScore;

    /**
     * 风险评估
     */
    private String riskAssessment;

    /**
     * 证据分析列表
     */
    private List<EvidenceResult> results;

    /**
     * 质证要点
     */
    private List<String> crossExaminationPoints;

    /**
     * 补强建议
     */
    private List<String> reinforcementSuggestions;

    /**
     * 证据链建议
     */
    private String chainSuggestion;

    // Getters and Setters
    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }

    public String getChainIntegrity() { return chainIntegrity; }
    public void setChainIntegrity(String chainIntegrity) { this.chainIntegrity = chainIntegrity; }

    public Integer getOverallScore() { return overallScore; }
    public void setOverallScore(Integer overallScore) { this.overallScore = overallScore; }

    public String getRiskAssessment() { return riskAssessment; }
    public void setRiskAssessment(String riskAssessment) { this.riskAssessment = riskAssessment; }

    public List<EvidenceResult> getResults() { return results; }
    public void setResults(List<EvidenceResult> results) { this.results = results; }

    public List<String> getCrossExaminationPoints() { return crossExaminationPoints; }
    public void setCrossExaminationPoints(List<String> crossExaminationPoints) { this.crossExaminationPoints = crossExaminationPoints; }

    public List<String> getReinforcementSuggestions() { return reinforcementSuggestions; }
    public void setReinforcementSuggestions(List<String> reinforcementSuggestions) { this.reinforcementSuggestions = reinforcementSuggestions; }

    public String getChainSuggestion() { return chainSuggestion; }
    public void setChainSuggestion(String chainSuggestion) { this.chainSuggestion = chainSuggestion; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Integer totalCount;
        private String chainIntegrity;
        private Integer overallScore;
        private String riskAssessment;
        private List<EvidenceResult> results;
        private List<String> crossExaminationPoints;
        private List<String> reinforcementSuggestions;
        private String chainSuggestion;

        public Builder totalCount(Integer totalCount) { this.totalCount = totalCount; return this; }
        public Builder chainIntegrity(String chainIntegrity) { this.chainIntegrity = chainIntegrity; return this; }
        public Builder overallScore(Integer overallScore) { this.overallScore = overallScore; return this; }
        public Builder riskAssessment(String riskAssessment) { this.riskAssessment = riskAssessment; return this; }
        public Builder results(List<EvidenceResult> results) { this.results = results; return this; }
        public Builder crossExaminationPoints(List<String> crossExaminationPoints) { this.crossExaminationPoints = crossExaminationPoints; return this; }
        public Builder reinforcementSuggestions(List<String> reinforcementSuggestions) { this.reinforcementSuggestions = reinforcementSuggestions; return this; }
        public Builder chainSuggestion(String chainSuggestion) { this.chainSuggestion = chainSuggestion; return this; }

        public EvidenceAnalysisResponse build() {
            EvidenceAnalysisResponse response = new EvidenceAnalysisResponse();
            response.setTotalCount(totalCount);
            response.setChainIntegrity(chainIntegrity);
            response.setOverallScore(overallScore);
            response.setRiskAssessment(riskAssessment);
            response.setResults(results);
            response.setCrossExaminationPoints(crossExaminationPoints);
            response.setReinforcementSuggestions(reinforcementSuggestions);
            response.setChainSuggestion(chainSuggestion);
            return response;
        }
    }

    /**
     * 证据分析结果
     */
    public static class EvidenceResult {
        private String name;
        private String type;
        private Authenticity authenticity;
        private Legality legality;
        private Relevance relevance;
        private String probativeValue;
        private Integer probativeScore;
        private List<String> issues;
        private String crossExamination;
        private String reinforcement;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public Authenticity getAuthenticity() { return authenticity; }
        public void setAuthenticity(Authenticity authenticity) { this.authenticity = authenticity; }

        public Legality getLegality() { return legality; }
        public void setLegality(Legality legality) { this.legality = legality; }

        public Relevance getRelevance() { return relevance; }
        public void setRelevance(Relevance relevance) { this.relevance = relevance; }

        public String getProbativeValue() { return probativeValue; }
        public void setProbativeValue(String probativeValue) { this.probativeValue = probativeValue; }

        public Integer getProbativeScore() { return probativeScore; }
        public void setProbativeScore(Integer probativeScore) { this.probativeScore = probativeScore; }

        public List<String> getIssues() { return issues; }
        public void setIssues(List<String> issues) { this.issues = issues; }

        public String getCrossExamination() { return crossExamination; }
        public void setCrossExamination(String crossExamination) { this.crossExamination = crossExamination; }

        public String getReinforcement() { return reinforcement; }
        public void setReinforcement(String reinforcement) { this.reinforcement = reinforcement; }

        // Builder
        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String name;
            private String type;
            private Authenticity authenticity;
            private Legality legality;
            private Relevance relevance;
            private String probativeValue;
            private Integer probativeScore;
            private List<String> issues;
            private String crossExamination;
            private String reinforcement;

            public Builder name(String name) { this.name = name; return this; }
            public Builder type(String type) { this.type = type; return this; }
            public Builder authenticity(Authenticity authenticity) { this.authenticity = authenticity; return this; }
            public Builder legality(Legality legality) { this.legality = legality; return this; }
            public Builder relevance(Relevance relevance) { this.relevance = relevance; return this; }
            public Builder probativeValue(String probativeValue) { this.probativeValue = probativeValue; return this; }
            public Builder probativeScore(Integer probativeScore) { this.probativeScore = probativeScore; return this; }
            public Builder issues(List<String> issues) { this.issues = issues; return this; }
            public Builder crossExamination(String crossExamination) { this.crossExamination = crossExamination; return this; }
            public Builder reinforcement(String reinforcement) { this.reinforcement = reinforcement; return this; }

            public EvidenceResult build() {
                EvidenceResult result = new EvidenceResult();
                result.setName(name);
                result.setType(type);
                result.setAuthenticity(authenticity);
                result.setLegality(legality);
                result.setRelevance(relevance);
                result.setProbativeValue(probativeValue);
                result.setProbativeScore(probativeScore);
                result.setIssues(issues);
                result.setCrossExamination(crossExamination);
                result.setReinforcement(reinforcement);
                return result;
            }
        }

        /**
         * 真实性分析
         */
        public static class Authenticity {
            private String result;
            private String analysis;
            private String suggestion;

            public Authenticity() {}

            public Authenticity(String result, String analysis, String suggestion) {
                this.result = result;
                this.analysis = analysis;
                this.suggestion = suggestion;
            }

            // Getters and Setters
            public String getResult() { return result; }
            public void setResult(String result) { this.result = result; }

            public String getAnalysis() { return analysis; }
            public void setAnalysis(String analysis) { this.analysis = analysis; }

            public String getSuggestion() { return suggestion; }
            public void setSuggestion(String suggestion) { this.suggestion = suggestion; }

            // Builder
            public static Builder builder() { return new Builder(); }

            public static class Builder {
                private String result;
                private String analysis;
                private String suggestion;

                public Builder result(String result) { this.result = result; return this; }
                public Builder analysis(String analysis) { this.analysis = analysis; return this; }
                public Builder suggestion(String suggestion) { this.suggestion = suggestion; return this; }

                public Authenticity build() {
                    return new Authenticity(result, analysis, suggestion);
                }
            }
        }

        /**
         * 合法性分析
         */
        public static class Legality {
            private String result;
            private String analysis;
            private String suggestion;

            public Legality() {}

            public Legality(String result, String analysis, String suggestion) {
                this.result = result;
                this.analysis = analysis;
                this.suggestion = suggestion;
            }

            // Getters and Setters
            public String getResult() { return result; }
            public void setResult(String result) { this.result = result; }

            public String getAnalysis() { return analysis; }
            public void setAnalysis(String analysis) { this.analysis = analysis; }

            public String getSuggestion() { return suggestion; }
            public void setSuggestion(String suggestion) { this.suggestion = suggestion; }

            // Builder
            public static Builder builder() { return new Builder(); }

            public static class Builder {
                private String result;
                private String analysis;
                private String suggestion;

                public Builder result(String result) { this.result = result; return this; }
                public Builder analysis(String analysis) { this.analysis = analysis; return this; }
                public Builder suggestion(String suggestion) { this.suggestion = suggestion; return this; }

                public Legality build() {
                    return new Legality(result, analysis, suggestion);
                }
            }
        }

        /**
         * 关联性分析
         */
        public static class Relevance {
            private String result;
            private String analysis;
            private String suggestion;

            public Relevance() {}

            public Relevance(String result, String analysis, String suggestion) {
                this.result = result;
                this.analysis = analysis;
                this.suggestion = suggestion;
            }

            // Getters and Setters
            public String getResult() { return result; }
            public void setResult(String result) { this.result = result; }

            public String getAnalysis() { return analysis; }
            public void setAnalysis(String analysis) { this.analysis = analysis; }

            public String getSuggestion() { return suggestion; }
            public void setSuggestion(String suggestion) { this.suggestion = suggestion; }

            // Builder
            public static Builder builder() { return new Builder(); }

            public static class Builder {
                private String result;
                private String analysis;
                private String suggestion;

                public Builder result(String result) { this.result = result; return this; }
                public Builder analysis(String analysis) { this.analysis = analysis; return this; }
                public Builder suggestion(String suggestion) { this.suggestion = suggestion; return this; }

                public Relevance build() {
                    return new Relevance(result, analysis, suggestion);
                }
            }
        }
    }
}
