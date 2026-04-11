package com.xiaoli.legal.analysis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 分析服务配置
 */
@Configuration
@ConfigurationProperties(prefix = "analysis")
public class AnalysisConfig {

    private AiConfig ai = new AiConfig();
    private FeeConfig fee = new FeeConfig();

    public AiConfig getAi() { return ai; }
    public void setAi(AiConfig ai) { this.ai = ai; }
    public FeeConfig getFee() { return fee; }
    public void setFee(FeeConfig fee) { this.fee = fee; }

    public static class AiConfig {
        private boolean enabled = true;
        private int maxRelatedCases = 5;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public int getMaxRelatedCases() { return maxRelatedCases; }
        public void setMaxRelatedCases(int maxRelatedCases) { this.maxRelatedCases = maxRelatedCases; }
    }

    public static class FeeConfig {
        private boolean enabled = true;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }
}
