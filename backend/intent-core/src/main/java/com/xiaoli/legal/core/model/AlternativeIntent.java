package com.xiaoli.legal.core.model;

import com.xiaoli.legal.core.IntentType;

/**
 * 备选意图
 */
public class AlternativeIntent {

    /**
     * 意图类型
     */
    private IntentType intent;

    /**
     * 置信度
     */
    private Double confidence;

    public AlternativeIntent() {}

    public AlternativeIntent(IntentType intent, Double confidence) {
        this.intent = intent;
        this.confidence = confidence;
    }

    // Getters and Setters
    public IntentType getIntent() { return intent; }
    public void setIntent(IntentType intent) { this.intent = intent; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private IntentType intent;
        private Double confidence;

        public Builder intent(IntentType intent) { this.intent = intent; return this; }
        public Builder confidence(Double confidence) { this.confidence = confidence; return this; }

        public AlternativeIntent build() {
            return new AlternativeIntent(intent, confidence);
        }
    }
}
