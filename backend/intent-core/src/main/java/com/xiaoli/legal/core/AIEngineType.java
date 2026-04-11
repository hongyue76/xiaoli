package com.xiaoli.legal.core;

/**
 * AI引擎类型
 */
public enum AIEngineType {

    /**
     * 规则引擎 - 基于预定义规则的快速响应
     */
    RULE_BASED("规则引擎", "基于预定义规则的快速响应"),

    /**
     * AI大模型 - 基于大语言模型的智能生成
     */
    LLM_BASED("AI大模型", "基于大语言模型的智能生成");

    private final String displayName;
    private final String description;

    AIEngineType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
