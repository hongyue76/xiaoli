package com.xiaoli.ai.common.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 意图分类结果
 */
public class Intent {
    
    /**
     * 意图类型：PROFESSIONAL（专业法律问题）或 GENERAL（通用问题）
     */
    private IntentType type;

    /**
     * 是否需要检索相关法律信息
     */
    private boolean needSearch;

    /**
     * 原始用户问题
     */
    private String originalQuestion;

    /**
     * 置信度（0-1）
     */
    @JsonProperty("confidence")
    private Double confidence;

    /**
     * 意图判断的详细原因
     */
    private String reason;

    // Getters and Setters
    public IntentType getType() { return type; }
    public void setType(IntentType type) { this.type = type; }

    public boolean isNeedSearch() { return needSearch; }
    public void setNeedSearch(boolean needSearch) { this.needSearch = needSearch; }

    public String getOriginalQuestion() { return originalQuestion; }
    public void setOriginalQuestion(String originalQuestion) { this.originalQuestion = originalQuestion; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    /**
     * 判断是否为专业法律问题
     */
    public boolean isProfessional() {
        return type == IntentType.PROFESSIONAL;
    }

    /**
     * 判断是否需要检索
     */
    public boolean requiresSearch() {
        return needSearch || (type != null && type.needSearch);
    }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private IntentType type;
        private boolean needSearch;
        private String originalQuestion;
        private Double confidence;
        private String reason;

        public Builder type(IntentType type) { this.type = type; return this; }
        public Builder needSearch(boolean needSearch) { this.needSearch = needSearch; return this; }
        public Builder originalQuestion(String originalQuestion) { this.originalQuestion = originalQuestion; return this; }
        public Builder confidence(Double confidence) { this.confidence = confidence; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }

        public Intent build() {
            Intent intent = new Intent();
            intent.type = this.type;
            intent.needSearch = this.needSearch;
            intent.originalQuestion = this.originalQuestion;
            intent.confidence = this.confidence;
            intent.reason = this.reason;
            return intent;
        }
    }

    /**
     * 意图类型枚举
     */
    public enum IntentType {
        /** 专业法律问题 - 需要调用得理API检索案例和法规 */
        PROFESSIONAL("PROFESSIONAL", "专业法律问题", true),

        /** 通用问题 - 直接由大模型回答，无需检索 */
        GENERAL("GENERAL", "通用问题", false),

        /** 闲聊 - 日常对话 */
        CHAT("CHAT", "闲聊", false),

        /** 天气查询 */
        WEATHER("WEATHER", "天气查询", false),

        /** 内容总结 */
        SUMMARY("SUMMARY", "内容总结", false),

        /** 不明确 - 需要进一步确认 */
        AMBIGUOUS("AMBIGUOUS", "意图不明确", false);

        private final String code;
        private final String description;
        private final boolean needSearch;

        IntentType(String code, String description, boolean needSearch) {
            this.code = code;
            this.description = description;
            this.needSearch = needSearch;
        }
    }
}
