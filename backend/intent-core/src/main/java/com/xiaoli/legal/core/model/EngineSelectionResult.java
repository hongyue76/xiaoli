package com.xiaoli.legal.core.model;

import com.xiaoli.legal.core.AIEngineType;
import com.xiaoli.legal.core.IntentType;

/**
 * 引擎选择结果
 */
public class EngineSelectionResult {

    /**
     * 选择的引擎类型
     */
    private AIEngineType engineType;

    /**
     * 引擎选择原因
     */
    private String reason;

    /**
     * 期望响应时间（毫秒）
     */
    private Long expectedResponseTime;

    /**
     * 成本预估（tokens或金额）
     */
    private String estimatedCost;

    /**
     * 是否使用混合模式
     */
    private Boolean hybridMode;

    /**
     * 混合模式中的规则引擎占比
     */
    private Double ruleEngineRatio;

    // Getters and Setters
    public AIEngineType getEngineType() { return engineType; }
    public void setEngineType(AIEngineType engineType) { this.engineType = engineType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Long getExpectedResponseTime() { return expectedResponseTime; }
    public void setExpectedResponseTime(Long expectedResponseTime) { this.expectedResponseTime = expectedResponseTime; }

    public String getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(String estimatedCost) { this.estimatedCost = estimatedCost; }

    public Boolean getHybridMode() { return hybridMode; }
    public void setHybridMode(Boolean hybridMode) { this.hybridMode = hybridMode; }

    public Double getRuleEngineRatio() { return ruleEngineRatio; }
    public void setRuleEngineRatio(Double ruleEngineRatio) { this.ruleEngineRatio = ruleEngineRatio; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private AIEngineType engineType;
        private String reason;
        private Long expectedResponseTime;
        private String estimatedCost;
        private Boolean hybridMode;
        private Double ruleEngineRatio;

        public Builder engineType(AIEngineType engineType) { this.engineType = engineType; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder expectedResponseTime(Long expectedResponseTime) { this.expectedResponseTime = expectedResponseTime; return this; }
        public Builder estimatedCost(String estimatedCost) { this.estimatedCost = estimatedCost; return this; }
        public Builder hybridMode(Boolean hybridMode) { this.hybridMode = hybridMode; return this; }
        public Builder ruleEngineRatio(Double ruleEngineRatio) { this.ruleEngineRatio = ruleEngineRatio; return this; }

        public EngineSelectionResult build() {
            EngineSelectionResult result = new EngineSelectionResult();
            result.setEngineType(engineType);
            result.setReason(reason);
            result.setExpectedResponseTime(expectedResponseTime);
            result.setEstimatedCost(estimatedCost);
            result.setHybridMode(hybridMode);
            result.setRuleEngineRatio(ruleEngineRatio);
            return result;
        }
    }
}
