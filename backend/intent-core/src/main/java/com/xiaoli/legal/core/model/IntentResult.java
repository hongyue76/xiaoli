package com.xiaoli.legal.core.model;

import com.xiaoli.legal.core.IntentType;

/**
 * 意图识别结果
 */
public class IntentResult {

    /**
     * 识别出的意图类型
     */
    private IntentType intent;

    /**
     * 置信度 0-1
     */
    private Double confidence;

    /**
     * 提取的关键词
     */
    private String[] keywords;

    /**
     * 意图说明
     */
    private String explanation;

    /**
     * 备选意图（置信度较低的候选）
     */
    private AlternativeIntent[] alternatives;

    // Getters and Setters
    public IntentType getIntent() { return intent; }
    public void setIntent(IntentType intent) { this.intent = intent; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public String[] getKeywords() { return keywords; }
    public void setKeywords(String[] keywords) { this.keywords = keywords; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public AlternativeIntent[] getAlternatives() { return alternatives; }
    public void setAlternatives(AlternativeIntent[] alternatives) { this.alternatives = alternatives; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private IntentType intent;
        private Double confidence;
        private String[] keywords;
        private String explanation;
        private AlternativeIntent[] alternatives;

        public Builder intent(IntentType intent) { this.intent = intent; return this; }
        public Builder confidence(Double confidence) { this.confidence = confidence; return this; }
        public Builder keywords(String[] keywords) { this.keywords = keywords; return this; }
        public Builder explanation(String explanation) { this.explanation = explanation; return this; }
        public Builder alternatives(AlternativeIntent[] alternatives) { this.alternatives = alternatives; return this; }

        public IntentResult build() {
            IntentResult result = new IntentResult();
            result.setIntent(intent);
            result.setConfidence(confidence);
            result.setKeywords(keywords);
            result.setExplanation(explanation);
            result.setAlternatives(alternatives);
            return result;
        }
    }
}
