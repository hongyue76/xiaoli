package com.xiaoli.legal.common.ai.dto;

import java.util.List;
import java.util.Map;

/**
 * AI对话请求
 */
public class ChatRequest {

    /**
     * 模型
     */
    private String model;

    /**
     * 消息列表
     */
    private List<ChatMessage> messages;

    /**
     * 温度 (0-2)
     */
    private Double temperature = 0.7;

    /**
     * 最大token数
     */
    private Integer maxTokens = 2048;

    /**
     * 频率惩罚
     */
    private Double frequencyPenalty = 0.0;

    /**
     * 存在惩罚
     */
    private Double presencePenalty = 0.0;

    /**
     * 流体输出
     */
    private Boolean stream = false;

    /**
     * 系统提示词
     */
    private String systemPrompt;

    /**
     * 额外参数
     */
    private Map<String, Object> extraParams;

    // Getters and Setters
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public List<ChatMessage> getMessages() { return messages; }
    public void setMessages(List<ChatMessage> messages) { this.messages = messages; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }

    public Double getFrequencyPenalty() { return frequencyPenalty; }
    public void setFrequencyPenalty(Double frequencyPenalty) { this.frequencyPenalty = frequencyPenalty; }

    public Double getPresencePenalty() { return presencePenalty; }
    public void setPresencePenalty(Double presencePenalty) { this.presencePenalty = presencePenalty; }

    public Boolean getStream() { return stream; }
    public void setStream(Boolean stream) { this.stream = stream; }

    public String getSystemPrompt() { return systemPrompt; }
    public void setSystemPrompt(String systemPrompt) { this.systemPrompt = systemPrompt; }

    public Map<String, Object> getExtraParams() { return extraParams; }
    public void setExtraParams(Map<String, Object> extraParams) { this.extraParams = extraParams; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String model;
        private List<ChatMessage> messages;
        private Double temperature = 0.7;
        private Integer maxTokens = 2048;
        private Double frequencyPenalty = 0.0;
        private Double presencePenalty = 0.0;
        private Boolean stream = false;
        private String systemPrompt;
        private Map<String, Object> extraParams;

        public Builder model(String model) { this.model = model; return this; }
        public Builder messages(List<ChatMessage> messages) { this.messages = messages; return this; }
        public Builder temperature(Double temperature) { this.temperature = temperature; return this; }
        public Builder maxTokens(Integer maxTokens) { this.maxTokens = maxTokens; return this; }
        public Builder frequencyPenalty(Double frequencyPenalty) { this.frequencyPenalty = frequencyPenalty; return this; }
        public Builder presencePenalty(Double presencePenalty) { this.presencePenalty = presencePenalty; return this; }
        public Builder stream(Boolean stream) { this.stream = stream; return this; }
        public Builder systemPrompt(String systemPrompt) { this.systemPrompt = systemPrompt; return this; }
        public Builder extraParams(Map<String, Object> extraParams) { this.extraParams = extraParams; return this; }

        public ChatRequest build() {
            ChatRequest req = new ChatRequest();
            req.model = this.model;
            req.messages = this.messages;
            req.temperature = this.temperature;
            req.maxTokens = this.maxTokens;
            req.frequencyPenalty = this.frequencyPenalty;
            req.presencePenalty = this.presencePenalty;
            req.stream = this.stream;
            req.systemPrompt = this.systemPrompt;
            req.extraParams = this.extraParams;
            return req;
        }
    }

    public static class ChatMessage {
        private String role;
        private String content;

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String role;
            private String content;

            public Builder role(String role) { this.role = role; return this; }
            public Builder content(String content) { this.content = content; return this; }

            public ChatMessage build() {
                ChatMessage msg = new ChatMessage();
                msg.role = this.role;
                msg.content = this.content;
                return msg;
            }
        }
    }
}
