package com.xiaoli.legal.analysis.model.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 案件分析响应
 */
public class CaseAnalysisResponse {

    private Long analysisId;
    private String caseTitle;
    private String analysisType;
    private String factAnalysis;
    private String lawAnalysis;
    private List<String> disputePoints;
    private List<LegalBasis> legalBasis;
    private String strategy;
    private RiskAssessment riskAssessment;
    private List<String> suggestions;
    private List<RelatedCase> relatedCases;
    private Integer winProbability;
    private List<TimeNode> timeNodes;
    private LitigationFee litigationFee;
    private EvidenceAnalysis evidenceAnalysis;
    private String causeAnalysis;
    private List<LegalRelation> legalRelations;
    private List<FavorableLegalBasis> favorableLegalBasis;

    public Long getAnalysisId() { return analysisId; }
    public void setAnalysisId(Long analysisId) { this.analysisId = analysisId; }
    public String getCaseTitle() { return caseTitle; }
    public void setCaseTitle(String caseTitle) { this.caseTitle = caseTitle; }
    public String getAnalysisType() { return analysisType; }
    public void setAnalysisType(String analysisType) { this.analysisType = analysisType; }
    public String getFactAnalysis() { return factAnalysis; }
    public void setFactAnalysis(String factAnalysis) { this.factAnalysis = factAnalysis; }
    public String getLawAnalysis() { return lawAnalysis; }
    public void setLawAnalysis(String lawAnalysis) { this.lawAnalysis = lawAnalysis; }
    public List<String> getDisputePoints() { return disputePoints; }
    public void setDisputePoints(List<String> disputePoints) { this.disputePoints = disputePoints; }
    public List<LegalBasis> getLegalBasis() { return legalBasis; }
    public void setLegalBasis(List<LegalBasis> legalBasis) { this.legalBasis = legalBasis; }
    public String getStrategy() { return strategy; }
    public void setStrategy(String strategy) { this.strategy = strategy; }
    public RiskAssessment getRiskAssessment() { return riskAssessment; }
    public void setRiskAssessment(RiskAssessment riskAssessment) { this.riskAssessment = riskAssessment; }
    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
    public List<RelatedCase> getRelatedCases() { return relatedCases; }
    public void setRelatedCases(List<RelatedCase> relatedCases) { this.relatedCases = relatedCases; }
    public Integer getWinProbability() { return winProbability; }
    public void setWinProbability(Integer winProbability) { this.winProbability = winProbability; }
    public List<TimeNode> getTimeNodes() { return timeNodes; }
    public void setTimeNodes(List<TimeNode> timeNodes) { this.timeNodes = timeNodes; }
    public LitigationFee getLitigationFee() { return litigationFee; }
    public void setLitigationFee(LitigationFee litigationFee) { this.litigationFee = litigationFee; }
    public EvidenceAnalysis getEvidenceAnalysis() { return evidenceAnalysis; }
    public void setEvidenceAnalysis(EvidenceAnalysis evidenceAnalysis) { this.evidenceAnalysis = evidenceAnalysis; }
    public String getCauseAnalysis() { return causeAnalysis; }
    public void setCauseAnalysis(String causeAnalysis) { this.causeAnalysis = causeAnalysis; }
    public List<LegalRelation> getLegalRelations() { return legalRelations; }
    public void setLegalRelations(List<LegalRelation> legalRelations) { this.legalRelations = legalRelations; }
    public List<FavorableLegalBasis> getFavorableLegalBasis() { return favorableLegalBasis; }
    public void setFavorableLegalBasis(List<FavorableLegalBasis> favorableLegalBasis) { this.favorableLegalBasis = favorableLegalBasis; }

    public CaseAnalysisResponse() {}

    public CaseAnalysisResponse(Long analysisId, String caseTitle, String analysisType, String factAnalysis,
            String lawAnalysis, List<String> disputePoints, List<LegalBasis> legalBasis, String strategy,
            RiskAssessment riskAssessment, List<String> suggestions, List<RelatedCase> relatedCases,
            Integer winProbability, List<TimeNode> timeNodes, LitigationFee litigationFee,
            EvidenceAnalysis evidenceAnalysis, String causeAnalysis, List<LegalRelation> legalRelations,
            List<FavorableLegalBasis> favorableLegalBasis) {
        this.analysisId = analysisId;
        this.caseTitle = caseTitle;
        this.analysisType = analysisType;
        this.factAnalysis = factAnalysis;
        this.lawAnalysis = lawAnalysis;
        this.disputePoints = disputePoints;
        this.legalBasis = legalBasis;
        this.strategy = strategy;
        this.riskAssessment = riskAssessment;
        this.suggestions = suggestions;
        this.relatedCases = relatedCases;
        this.winProbability = winProbability;
        this.timeNodes = timeNodes;
        this.litigationFee = litigationFee;
        this.evidenceAnalysis = evidenceAnalysis;
        this.causeAnalysis = causeAnalysis;
        this.legalRelations = legalRelations;
        this.favorableLegalBasis = favorableLegalBasis;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long analysisId;
        private String caseTitle;
        private String analysisType;
        private String factAnalysis;
        private String lawAnalysis;
        private List<String> disputePoints;
        private List<LegalBasis> legalBasis;
        private String strategy;
        private RiskAssessment riskAssessment;
        private List<String> suggestions;
        private List<RelatedCase> relatedCases;
        private Integer winProbability;
        private List<TimeNode> timeNodes;
        private LitigationFee litigationFee;
        private EvidenceAnalysis evidenceAnalysis;
        private String causeAnalysis;
        private List<LegalRelation> legalRelations;
        private List<FavorableLegalBasis> favorableLegalBasis;

        public Builder analysisId(Long analysisId) { this.analysisId = analysisId; return this; }
        public Builder caseTitle(String caseTitle) { this.caseTitle = caseTitle; return this; }
        public Builder analysisType(String analysisType) { this.analysisType = analysisType; return this; }
        public Builder factAnalysis(String factAnalysis) { this.factAnalysis = factAnalysis; return this; }
        public Builder lawAnalysis(String lawAnalysis) { this.lawAnalysis = lawAnalysis; return this; }
        public Builder disputePoints(List<String> disputePoints) { this.disputePoints = disputePoints; return this; }
        public Builder legalBasis(List<LegalBasis> legalBasis) { this.legalBasis = legalBasis; return this; }
        public Builder strategy(String strategy) { this.strategy = strategy; return this; }
        public Builder riskAssessment(RiskAssessment riskAssessment) { this.riskAssessment = riskAssessment; return this; }
        public Builder suggestions(List<String> suggestions) { this.suggestions = suggestions; return this; }
        public Builder relatedCases(List<RelatedCase> relatedCases) { this.relatedCases = relatedCases; return this; }
        public Builder winProbability(Integer winProbability) { this.winProbability = winProbability; return this; }
        public Builder timeNodes(List<TimeNode> timeNodes) { this.timeNodes = timeNodes; return this; }
        public Builder litigationFee(LitigationFee litigationFee) { this.litigationFee = litigationFee; return this; }
        public Builder evidenceAnalysis(EvidenceAnalysis evidenceAnalysis) { this.evidenceAnalysis = evidenceAnalysis; return this; }
        public Builder causeAnalysis(String causeAnalysis) { this.causeAnalysis = causeAnalysis; return this; }
        public Builder legalRelations(List<LegalRelation> legalRelations) { this.legalRelations = legalRelations; return this; }
        public Builder favorableLegalBasis(List<FavorableLegalBasis> favorableLegalBasis) { this.favorableLegalBasis = favorableLegalBasis; return this; }

        public CaseAnalysisResponse build() {
            return new CaseAnalysisResponse(analysisId, caseTitle, analysisType, factAnalysis, lawAnalysis,
                    disputePoints, legalBasis, strategy, riskAssessment, suggestions, relatedCases,
                    winProbability, timeNodes, litigationFee, evidenceAnalysis, causeAnalysis,
                    legalRelations, favorableLegalBasis);
        }
    }

    /**
     * 法律依据
     */
    public static class LegalBasis {
        private String lawName;
        private String article;
        private String content;

        public String getLawName() { return lawName; }
        public void setLawName(String lawName) { this.lawName = lawName; }
        public String getArticle() { return article; }
        public void setArticle(String article) { this.article = article; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public LegalBasis() {}
        public LegalBasis(String lawName, String article, String content) {
            this.lawName = lawName;
            this.article = article;
            this.content = content;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String lawName;
            private String article;
            private String content;

            public Builder lawName(String lawName) { this.lawName = lawName; return this; }
            public Builder article(String article) { this.article = article; return this; }
            public Builder content(String content) { this.content = content; return this; }

            public LegalBasis build() {
                return new LegalBasis(lawName, article, content);
            }
        }
    }

    /**
     * 风险评估
     */
    public static class RiskAssessment {
        private String level;
        private Integer score;
        private List<RiskItem> items;

        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        public Integer getScore() { return score; }
        public void setScore(Integer score) { this.score = score; }
        public List<RiskItem> getItems() { return items; }
        public void setItems(List<RiskItem> items) { this.items = items; }

        public RiskAssessment() {}
        public RiskAssessment(String level, Integer score, List<RiskItem> items) {
            this.level = level;
            this.score = score;
            this.items = items;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String level;
            private Integer score;
            private List<RiskItem> items;

            public Builder level(String level) { this.level = level; return this; }
            public Builder score(Integer score) { this.score = score; return this; }
            public Builder items(List<RiskItem> items) { this.items = items; return this; }

            public RiskAssessment build() {
                return new RiskAssessment(level, score, items);
            }
        }
    }

    /**
     * 风险项
     */
    public static class RiskItem {
        private String description;
        private String severity;
        private String mitigation;

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        public String getMitigation() { return mitigation; }
        public void setMitigation(String mitigation) { this.mitigation = mitigation; }

        public RiskItem() {}
        public RiskItem(String description, String severity, String mitigation) {
            this.description = description;
            this.severity = severity;
            this.mitigation = mitigation;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String description;
            private String severity;
            private String mitigation;

            public Builder description(String description) { this.description = description; return this; }
            public Builder severity(String severity) { this.severity = severity; return this; }
            public Builder mitigation(String mitigation) { this.mitigation = mitigation; return this; }

            public RiskItem build() {
                return new RiskItem(description, severity, mitigation);
            }
        }
    }

    /**
     * 风险项（别名）
     */
    public static class RiskItemAlt {
        private String description;
        private String severity;
        private String mitigation;

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        public String getMitigation() { return mitigation; }
        public void setMitigation(String mitigation) { this.mitigation = mitigation; }

        public RiskItemAlt() {}
        public RiskItemAlt(String description, String severity, String mitigation) {
            this.description = description;
            this.severity = severity;
            this.mitigation = mitigation;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String description;
            private String severity;
            private String mitigation;

            public Builder description(String description) { this.description = description; return this; }
            public Builder severity(String severity) { this.severity = severity; return this; }
            public Builder mitigation(String mitigation) { this.mitigation = mitigation; return this; }

            public RiskItemAlt build() {
                return new RiskItemAlt(description, severity, mitigation);
            }
        }
    }

    /**
     * 相关案例
     */
    public static class RelatedCase {
        private Long caseId;
        private String title;
        private String court;
        private String result;
        private Double similarity;

        public Long getCaseId() { return caseId; }
        public void setCaseId(Long caseId) { this.caseId = caseId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getCourt() { return court; }
        public void setCourt(String court) { this.court = court; }
        public String getResult() { return result; }
        public void setResult(String result) { this.result = result; }
        public Double getSimilarity() { return similarity; }
        public void setSimilarity(Double similarity) { this.similarity = similarity; }

        public RelatedCase() {}
        public RelatedCase(Long caseId, String title, String court, String result, Double similarity) {
            this.caseId = caseId;
            this.title = title;
            this.court = court;
            this.result = result;
            this.similarity = similarity;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private Long caseId;
            private String title;
            private String court;
            private String result;
            private Double similarity;

            public Builder caseId(Long caseId) { this.caseId = caseId; return this; }
            public Builder title(String title) { this.title = title; return this; }
            public Builder court(String court) { this.court = court; return this; }
            public Builder result(String result) { this.result = result; return this; }
            public Builder similarity(Double similarity) { this.similarity = similarity; return this; }

            public RelatedCase build() {
                return new RelatedCase(caseId, title, court, result, similarity);
            }
        }
    }

    /**
     * 时间节点
     */
    public static class TimeNode {
        private String stage;
        private String deadline;
        private String action;

        public String getStage() { return stage; }
        public void setStage(String stage) { this.stage = stage; }
        public String getDeadline() { return deadline; }
        public void setDeadline(String deadline) { this.deadline = deadline; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }

        public TimeNode() {}
        public TimeNode(String stage, String deadline, String action) {
            this.stage = stage;
            this.deadline = deadline;
            this.action = action;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String stage;
            private String deadline;
            private String action;

            public Builder stage(String stage) { this.stage = stage; return this; }
            public Builder deadline(String deadline) { this.deadline = deadline; return this; }
            public Builder action(String action) { this.action = action; return this; }

            public TimeNode build() {
                return new TimeNode(stage, deadline, action);
            }
        }
    }

    /**
     * 诉讼费用
     */
    public static class LitigationFee {
        private BigDecimal claimAmount;
        private BigDecimal caseFee;
        private BigDecimal lawyerFee;
        private BigDecimal otherFees;
        private BigDecimal totalFee;
        private List<FeeItem> feeItems;
        private String paymentTip;

        public BigDecimal getClaimAmount() { return claimAmount; }
        public void setClaimAmount(BigDecimal claimAmount) { this.claimAmount = claimAmount; }
        public BigDecimal getCaseFee() { return caseFee; }
        public void setCaseFee(BigDecimal caseFee) { this.caseFee = caseFee; }
        public BigDecimal getLawyerFee() { return lawyerFee; }
        public void setLawyerFee(BigDecimal lawyerFee) { this.lawyerFee = lawyerFee; }
        public BigDecimal getOtherFees() { return otherFees; }
        public void setOtherFees(BigDecimal otherFees) { this.otherFees = otherFees; }
        public BigDecimal getTotalFee() { return totalFee; }
        public void setTotalFee(BigDecimal totalFee) { this.totalFee = totalFee; }
        public List<FeeItem> getFeeItems() { return feeItems; }
        public void setFeeItems(List<FeeItem> feeItems) { this.feeItems = feeItems; }
        public String getPaymentTip() { return paymentTip; }
        public void setPaymentTip(String paymentTip) { this.paymentTip = paymentTip; }

        public LitigationFee() {}
        public LitigationFee(BigDecimal claimAmount, BigDecimal caseFee, BigDecimal lawyerFee,
                BigDecimal otherFees, BigDecimal totalFee, List<FeeItem> feeItems, String paymentTip) {
            this.claimAmount = claimAmount;
            this.caseFee = caseFee;
            this.lawyerFee = lawyerFee;
            this.otherFees = otherFees;
            this.totalFee = totalFee;
            this.feeItems = feeItems;
            this.paymentTip = paymentTip;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private BigDecimal claimAmount;
            private BigDecimal caseFee;
            private BigDecimal lawyerFee;
            private BigDecimal otherFees;
            private BigDecimal totalFee;
            private List<FeeItem> feeItems;
            private String paymentTip;

            public Builder claimAmount(BigDecimal claimAmount) { this.claimAmount = claimAmount; return this; }
            public Builder caseFee(BigDecimal caseFee) { this.caseFee = caseFee; return this; }
            public Builder lawyerFee(BigDecimal lawyerFee) { this.lawyerFee = lawyerFee; return this; }
            public Builder otherFees(BigDecimal otherFees) { this.otherFees = otherFees; return this; }
            public Builder totalFee(BigDecimal totalFee) { this.totalFee = totalFee; return this; }
            public Builder feeItems(List<FeeItem> feeItems) { this.feeItems = feeItems; return this; }
            public Builder paymentTip(String paymentTip) { this.paymentTip = paymentTip; return this; }

            public LitigationFee build() {
                return new LitigationFee(claimAmount, caseFee, lawyerFee, otherFees, totalFee, feeItems, paymentTip);
            }
        }
    }

    /**
     * 费用项目
     */
    public static class FeeItem {
        private String name;
        private String description;
        private BigDecimal amount;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public FeeItem() {}
        public FeeItem(String name, String description, BigDecimal amount) {
            this.name = name;
            this.description = description;
            this.amount = amount;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String name;
            private String description;
            private BigDecimal amount;

            public Builder name(String name) { this.name = name; return this; }
            public Builder description(String description) { this.description = description; return this; }
            public Builder amount(BigDecimal amount) { this.amount = amount; return this; }

            public FeeItem build() {
                return new FeeItem(name, description, amount);
            }
        }
    }

    /**
     * 证据分析
     */
    public static class EvidenceAnalysis {
        private Integer score;
        private String level;
        private List<String> advantages;
        private List<String> weaknesses;
        private List<String> suggestions;
        private List<EvidenceCatalog> evidenceCatalog;
        private EvidenceCapabilitySummary capabilitySummary;
        private LawRelationSummary lawRelationSummary;

        public Integer getScore() { return score; }
        public void setScore(Integer score) { this.score = score; }
        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        public List<String> getAdvantages() { return advantages; }
        public void setAdvantages(List<String> advantages) { this.advantages = advantages; }
        public List<String> getWeaknesses() { return weaknesses; }
        public void setWeaknesses(List<String> weaknesses) { this.weaknesses = weaknesses; }
        public List<String> getSuggestions() { return suggestions; }
        public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
        public List<EvidenceCatalog> getEvidenceCatalog() { return evidenceCatalog; }
        public void setEvidenceCatalog(List<EvidenceCatalog> evidenceCatalog) { this.evidenceCatalog = evidenceCatalog; }
        public EvidenceCapabilitySummary getCapabilitySummary() { return capabilitySummary; }
        public void setCapabilitySummary(EvidenceCapabilitySummary capabilitySummary) { this.capabilitySummary = capabilitySummary; }
        public LawRelationSummary getLawRelationSummary() { return lawRelationSummary; }
        public void setLawRelationSummary(LawRelationSummary lawRelationSummary) { this.lawRelationSummary = lawRelationSummary; }

        public EvidenceAnalysis() {}
        public EvidenceAnalysis(Integer score, String level, List<String> advantages, List<String> weaknesses,
                List<String> suggestions, List<EvidenceCatalog> evidenceCatalog,
                EvidenceCapabilitySummary capabilitySummary, LawRelationSummary lawRelationSummary) {
            this.score = score;
            this.level = level;
            this.advantages = advantages;
            this.weaknesses = weaknesses;
            this.suggestions = suggestions;
            this.evidenceCatalog = evidenceCatalog;
            this.capabilitySummary = capabilitySummary;
            this.lawRelationSummary = lawRelationSummary;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private Integer score;
            private String level;
            private List<String> advantages;
            private List<String> weaknesses;
            private List<String> suggestions;
            private List<EvidenceCatalog> evidenceCatalog;
            private EvidenceCapabilitySummary capabilitySummary;
            private LawRelationSummary lawRelationSummary;

            public Builder score(Integer score) { this.score = score; return this; }
            public Builder level(String level) { this.level = level; return this; }
            public Builder advantages(List<String> advantages) { this.advantages = advantages; return this; }
            public Builder weaknesses(List<String> weaknesses) { this.weaknesses = weaknesses; return this; }
            public Builder suggestions(List<String> suggestions) { this.suggestions = suggestions; return this; }
            public Builder evidenceCatalog(List<EvidenceCatalog> evidenceCatalog) { this.evidenceCatalog = evidenceCatalog; return this; }
            public Builder capabilitySummary(EvidenceCapabilitySummary capabilitySummary) { this.capabilitySummary = capabilitySummary; return this; }
            public Builder lawRelationSummary(LawRelationSummary lawRelationSummary) { this.lawRelationSummary = lawRelationSummary; return this; }

            public EvidenceAnalysis build() {
                return new EvidenceAnalysis(score, level, advantages, weaknesses, suggestions,
                        evidenceCatalog, capabilitySummary, lawRelationSummary);
            }
        }
    }

    /**
     * 证据能力分析摘要
     */
    public static class EvidenceCapabilitySummary {
        private Integer legalityScore;
        private Integer authenticityScore;
        private Integer relevanceScore;
        private String overallEvaluation;
        private List<String> legalityAnalysis;
        private List<String> authenticityAnalysis;
        private List<String> relevanceAnalysis;

        public Integer getLegalityScore() { return legalityScore; }
        public void setLegalityScore(Integer legalityScore) { this.legalityScore = legalityScore; }
        public Integer getAuthenticityScore() { return authenticityScore; }
        public void setAuthenticityScore(Integer authenticityScore) { this.authenticityScore = authenticityScore; }
        public Integer getRelevanceScore() { return relevanceScore; }
        public void setRelevanceScore(Integer relevanceScore) { this.relevanceScore = relevanceScore; }
        public String getOverallEvaluation() { return overallEvaluation; }
        public void setOverallEvaluation(String overallEvaluation) { this.overallEvaluation = overallEvaluation; }
        public List<String> getLegalityAnalysis() { return legalityAnalysis; }
        public void setLegalityAnalysis(List<String> legalityAnalysis) { this.legalityAnalysis = legalityAnalysis; }
        public List<String> getAuthenticityAnalysis() { return authenticityAnalysis; }
        public void setAuthenticityAnalysis(List<String> authenticityAnalysis) { this.authenticityAnalysis = authenticityAnalysis; }
        public List<String> getRelevanceAnalysis() { return relevanceAnalysis; }
        public void setRelevanceAnalysis(List<String> relevanceAnalysis) { this.relevanceAnalysis = relevanceAnalysis; }

        public EvidenceCapabilitySummary() {}
        public EvidenceCapabilitySummary(Integer legalityScore, Integer authenticityScore, Integer relevanceScore,
                String overallEvaluation, List<String> legalityAnalysis, List<String> authenticityAnalysis,
                List<String> relevanceAnalysis) {
            this.legalityScore = legalityScore;
            this.authenticityScore = authenticityScore;
            this.relevanceScore = relevanceScore;
            this.overallEvaluation = overallEvaluation;
            this.legalityAnalysis = legalityAnalysis;
            this.authenticityAnalysis = authenticityAnalysis;
            this.relevanceAnalysis = relevanceAnalysis;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private Integer legalityScore;
            private Integer authenticityScore;
            private Integer relevanceScore;
            private String overallEvaluation;
            private List<String> legalityAnalysis;
            private List<String> authenticityAnalysis;
            private List<String> relevanceAnalysis;

            public Builder legalityScore(Integer legalityScore) { this.legalityScore = legalityScore; return this; }
            public Builder authenticityScore(Integer authenticityScore) { this.authenticityScore = authenticityScore; return this; }
            public Builder relevanceScore(Integer relevanceScore) { this.relevanceScore = relevanceScore; return this; }
            public Builder overallEvaluation(String overallEvaluation) { this.overallEvaluation = overallEvaluation; return this; }
            public Builder legalityAnalysis(List<String> legalityAnalysis) { this.legalityAnalysis = legalityAnalysis; return this; }
            public Builder authenticityAnalysis(List<String> authenticityAnalysis) { this.authenticityAnalysis = authenticityAnalysis; return this; }
            public Builder relevanceAnalysis(List<String> relevanceAnalysis) { this.relevanceAnalysis = relevanceAnalysis; return this; }

            public EvidenceCapabilitySummary build() {
                return new EvidenceCapabilitySummary(legalityScore, authenticityScore, relevanceScore,
                        overallEvaluation, legalityAnalysis, authenticityAnalysis, relevanceAnalysis);
            }
        }
    }

    /**
     * 法条关联摘要
     */
    public static class LawRelationSummary {
        private Integer totalCount;
        private List<String> mainLaws;
        private List<String> missingLaws;

        public Integer getTotalCount() { return totalCount; }
        public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
        public List<String> getMainLaws() { return mainLaws; }
        public void setMainLaws(List<String> mainLaws) { this.mainLaws = mainLaws; }
        public List<String> getMissingLaws() { return missingLaws; }
        public void setMissingLaws(List<String> missingLaws) { this.missingLaws = missingLaws; }

        public LawRelationSummary() {}
        public LawRelationSummary(Integer totalCount, List<String> mainLaws, List<String> missingLaws) {
            this.totalCount = totalCount;
            this.mainLaws = mainLaws;
            this.missingLaws = missingLaws;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private Integer totalCount;
            private List<String> mainLaws;
            private List<String> missingLaws;

            public Builder totalCount(Integer totalCount) { this.totalCount = totalCount; return this; }
            public Builder mainLaws(List<String> mainLaws) { this.mainLaws = mainLaws; return this; }
            public Builder missingLaws(List<String> missingLaws) { this.missingLaws = missingLaws; return this; }

            public LawRelationSummary build() {
                return new LawRelationSummary(totalCount, mainLaws, missingLaws);
            }
        }
    }

    /**
     * 证据目录
     */
    public static class EvidenceCatalog {
        private String index;
        private String name;
        private String type;
        private String source;
        private String keyPoint;
        private String probativeValue;
        private String counterArgument;
        private String status;
        private EvidenceCapability capability;
        private List<EvidenceLawRelation> lawRelations;

        public String getIndex() { return index; }
        public void setIndex(String index) { this.index = index; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public String getKeyPoint() { return keyPoint; }
        public void setKeyPoint(String keyPoint) { this.keyPoint = keyPoint; }
        public String getProbativeValue() { return probativeValue; }
        public void setProbativeValue(String probativeValue) { this.probativeValue = probativeValue; }
        public String getCounterArgument() { return counterArgument; }
        public void setCounterArgument(String counterArgument) { this.counterArgument = counterArgument; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public EvidenceCapability getCapability() { return capability; }
        public void setCapability(EvidenceCapability capability) { this.capability = capability; }
        public List<EvidenceLawRelation> getLawRelations() { return lawRelations; }
        public void setLawRelations(List<EvidenceLawRelation> lawRelations) { this.lawRelations = lawRelations; }

        public EvidenceCatalog() {}
        public EvidenceCatalog(String index, String name, String type, String source, String keyPoint,
                String probativeValue, String counterArgument, String status,
                EvidenceCapability capability, List<EvidenceLawRelation> lawRelations) {
            this.index = index;
            this.name = name;
            this.type = type;
            this.source = source;
            this.keyPoint = keyPoint;
            this.probativeValue = probativeValue;
            this.counterArgument = counterArgument;
            this.status = status;
            this.capability = capability;
            this.lawRelations = lawRelations;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String index;
            private String name;
            private String type;
            private String source;
            private String keyPoint;
            private String probativeValue;
            private String counterArgument;
            private String status;
            private EvidenceCapability capability;
            private List<EvidenceLawRelation> lawRelations;

            public Builder index(String index) { this.index = index; return this; }
            public Builder name(String name) { this.name = name; return this; }
            public Builder type(String type) { this.type = type; return this; }
            public Builder source(String source) { this.source = source; return this; }
            public Builder keyPoint(String keyPoint) { this.keyPoint = keyPoint; return this; }
            public Builder probativeValue(String probativeValue) { this.probativeValue = probativeValue; return this; }
            public Builder counterArgument(String counterArgument) { this.counterArgument = counterArgument; return this; }
            public Builder status(String status) { this.status = status; return this; }
            public Builder capability(EvidenceCapability capability) { this.capability = capability; return this; }
            public Builder lawRelations(List<EvidenceLawRelation> lawRelations) { this.lawRelations = lawRelations; return this; }

            public EvidenceCatalog build() {
                return new EvidenceCatalog(index, name, type, source, keyPoint, probativeValue,
                        counterArgument, status, capability, lawRelations);
            }
        }
    }

    /**
     * 证据能力（证据三性）
     */
    public static class EvidenceCapability {
        private String legality;
        private String legalityNote;
        private String authenticity;
        private String authenticityNote;
        private String relevance;
        private String relevanceNote;
        private String capabilityLevel;

        public String getLegality() { return legality; }
        public void setLegality(String legality) { this.legality = legality; }
        public String getLegalityNote() { return legalityNote; }
        public void setLegalityNote(String legalityNote) { this.legalityNote = legalityNote; }
        public String getAuthenticity() { return authenticity; }
        public void setAuthenticity(String authenticity) { this.authenticity = authenticity; }
        public String getAuthenticityNote() { return authenticityNote; }
        public void setAuthenticityNote(String authenticityNote) { this.authenticityNote = authenticityNote; }
        public String getRelevance() { return relevance; }
        public void setRelevance(String relevance) { this.relevance = relevance; }
        public String getRelevanceNote() { return relevanceNote; }
        public void setRelevanceNote(String relevanceNote) { this.relevanceNote = relevanceNote; }
        public String getCapabilityLevel() { return capabilityLevel; }
        public void setCapabilityLevel(String capabilityLevel) { this.capabilityLevel = capabilityLevel; }

        public EvidenceCapability() {}
        public EvidenceCapability(String legality, String legalityNote, String authenticity,
                String authenticityNote, String relevance, String relevanceNote, String capabilityLevel) {
            this.legality = legality;
            this.legalityNote = legalityNote;
            this.authenticity = authenticity;
            this.authenticityNote = authenticityNote;
            this.relevance = relevance;
            this.relevanceNote = relevanceNote;
            this.capabilityLevel = capabilityLevel;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String legality;
            private String legalityNote;
            private String authenticity;
            private String authenticityNote;
            private String relevance;
            private String relevanceNote;
            private String capabilityLevel;

            public Builder legality(String legality) { this.legality = legality; return this; }
            public Builder legalityNote(String legalityNote) { this.legalityNote = legalityNote; return this; }
            public Builder authenticity(String authenticity) { this.authenticity = authenticity; return this; }
            public Builder authenticityNote(String authenticityNote) { this.authenticityNote = authenticityNote; return this; }
            public Builder relevance(String relevance) { this.relevance = relevance; return this; }
            public Builder relevanceNote(String relevanceNote) { this.relevanceNote = relevanceNote; return this; }
            public Builder capabilityLevel(String capabilityLevel) { this.capabilityLevel = capabilityLevel; return this; }

            public EvidenceCapability build() {
                return new EvidenceCapability(legality, legalityNote, authenticity, authenticityNote,
                        relevance, relevanceNote, capabilityLevel);
            }
        }
    }

    /**
     * 证据与法条关联
     */
    public static class EvidenceLawRelation {
        private String lawName;
        private String article;
        private String relationNote;
        private String priority;

        public String getLawName() { return lawName; }
        public void setLawName(String lawName) { this.lawName = lawName; }
        public String getArticle() { return article; }
        public void setArticle(String article) { this.article = article; }
        public String getRelationNote() { return relationNote; }
        public void setRelationNote(String relationNote) { this.relationNote = relationNote; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }

        public EvidenceLawRelation() {}
        public EvidenceLawRelation(String lawName, String article, String relationNote, String priority) {
            this.lawName = lawName;
            this.article = article;
            this.relationNote = relationNote;
            this.priority = priority;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String lawName;
            private String article;
            private String relationNote;
            private String priority;

            public Builder lawName(String lawName) { this.lawName = lawName; return this; }
            public Builder article(String article) { this.article = article; return this; }
            public Builder relationNote(String relationNote) { this.relationNote = relationNote; return this; }
            public Builder priority(String priority) { this.priority = priority; return this; }

            public EvidenceLawRelation build() {
                return new EvidenceLawRelation(lawName, article, relationNote, priority);
            }
        }
    }

    /**
     * 法律关系
     */
    public static class LegalRelation {
        private String type;
        private String description;
        private List<String> parties;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<String> getParties() { return parties; }
        public void setParties(List<String> parties) { this.parties = parties; }

        public LegalRelation() {}
        public LegalRelation(String type, String description, List<String> parties) {
            this.type = type;
            this.description = description;
            this.parties = parties;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String type;
            private String description;
            private List<String> parties;

            public Builder type(String type) { this.type = type; return this; }
            public Builder description(String description) { this.description = description; return this; }
            public Builder parties(List<String> parties) { this.parties = parties; return this; }

            public LegalRelation build() {
                return new LegalRelation(type, description, parties);
            }
        }
    }

    /**
     * 有利法条
     */
    public static class FavorableLegalBasis {
        private String lawName;
        private String article;
        private String content;
        private String applicableScenario;
        private String favorableReason;
        private List<String> supportingCases;
        private String difficulty;

        public String getLawName() { return lawName; }
        public void setLawName(String lawName) { this.lawName = lawName; }
        public String getArticle() { return article; }
        public void setArticle(String article) { this.article = article; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getApplicableScenario() { return applicableScenario; }
        public void setApplicableScenario(String applicableScenario) { this.applicableScenario = applicableScenario; }
        public String getFavorableReason() { return favorableReason; }
        public void setFavorableReason(String favorableReason) { this.favorableReason = favorableReason; }
        public List<String> getSupportingCases() { return supportingCases; }
        public void setSupportingCases(List<String> supportingCases) { this.supportingCases = supportingCases; }
        public String getDifficulty() { return difficulty; }
        public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

        public FavorableLegalBasis() {}
        public FavorableLegalBasis(String lawName, String article, String content, String applicableScenario,
                String favorableReason, List<String> supportingCases, String difficulty) {
            this.lawName = lawName;
            this.article = article;
            this.content = content;
            this.applicableScenario = applicableScenario;
            this.favorableReason = favorableReason;
            this.supportingCases = supportingCases;
            this.difficulty = difficulty;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String lawName;
            private String article;
            private String content;
            private String applicableScenario;
            private String favorableReason;
            private List<String> supportingCases;
            private String difficulty;

            public Builder lawName(String lawName) { this.lawName = lawName; return this; }
            public Builder article(String article) { this.article = article; return this; }
            public Builder content(String content) { this.content = content; return this; }
            public Builder applicableScenario(String applicableScenario) { this.applicableScenario = applicableScenario; return this; }
            public Builder favorableReason(String favorableReason) { this.favorableReason = favorableReason; return this; }
            public Builder supportingCases(List<String> supportingCases) { this.supportingCases = supportingCases; return this; }
            public Builder difficulty(String difficulty) { this.difficulty = difficulty; return this; }

            public FavorableLegalBasis build() {
                return new FavorableLegalBasis(lawName, article, content, applicableScenario,
                        favorableReason, supportingCases, difficulty);
            }
        }
    }
}
