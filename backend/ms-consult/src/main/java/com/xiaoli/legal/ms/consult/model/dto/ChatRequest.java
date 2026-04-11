package com.xiaoli.legal.ms.consult.model.dto;

import java.util.List;
import java.util.Map;

/**
 * 小理AI对话请求
 */
public class ChatRequest {

    /**
     * 模型名称
     */
    private String model;

    /**
     * 消息列表
     */
    private List<ChatMessage> messages;

    /**
     * 温度参数 (0-2)
     */
    private Double temperature = 0.7;

    /**
     * 最大token数
     */
    private Integer maxTokens = 2048;

    /**
     * 顶部采样
     */
    private Double topP = 1.0;

    /**
     * 是否流式输出
     */
    private Boolean stream = false;

    /**
     * 停止词列表
     */
    private List<String> stop;

    /**
     * 自定义参数
     */
    private Map<String, Object> extra;

    // Getters and Setters
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public List<ChatMessage> getMessages() { return messages; }
    public void setMessages(List<ChatMessage> messages) { this.messages = messages; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }

    public Double getTopP() { return topP; }
    public void setTopP(Double topP) { this.topP = topP; }

    public Boolean getStream() { return stream; }
    public void setStream(Boolean stream) { this.stream = stream; }

    public List<String> getStop() { return stop; }
    public void setStop(List<String> stop) { this.stop = stop; }

    public Map<String, Object> getExtra() { return extra; }
    public void setExtra(Map<String, Object> extra) { this.extra = extra; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String model;
        private List<ChatMessage> messages;
        private Double temperature = 0.7;
        private Integer maxTokens = 2048;
        private Double topP = 1.0;
        private Boolean stream = false;
        private List<String> stop;
        private Map<String, Object> extra;

        public Builder model(String model) { this.model = model; return this; }
        public Builder messages(List<ChatMessage> messages) { this.messages = messages; return this; }
        public Builder temperature(Double temperature) { this.temperature = temperature; return this; }
        public Builder maxTokens(Integer maxTokens) { this.maxTokens = maxTokens; return this; }
        public Builder topP(Double topP) { this.topP = topP; return this; }
        public Builder stream(Boolean stream) { this.stream = stream; return this; }
        public Builder stop(List<String> stop) { this.stop = stop; return this; }
        public Builder extra(Map<String, Object> extra) { this.extra = extra; return this; }

        public ChatRequest build() {
            ChatRequest request = new ChatRequest();
            request.setModel(model);
            request.setMessages(messages);
            request.setTemperature(temperature);
            request.setMaxTokens(maxTokens);
            request.setTopP(topP);
            request.setStream(stream);
            request.setStop(stop);
            request.setExtra(extra);
            return request;
        }
    }

    /**
     * 聊天消息
     */
    public static class ChatMessage {
        /**
         * 角色: system, user, assistant
         */
        private String role;

        /**
         * 消息内容
         */
        private String content;

        /**
         * 名称 (可选)
         */
        private String name;

        public ChatMessage() {}

        public ChatMessage(String role, String content, String name) {
            this.role = role;
            this.content = content;
            this.name = name;
        }

        // Getters and Setters
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        // Builder
        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String role;
            private String content;
            private String name;

            public Builder role(String role) { this.role = role; return this; }
            public Builder content(String content) { this.content = content; return this; }
            public Builder name(String name) { this.name = name; return this; }

            public ChatMessage build() {
                return new ChatMessage(role, content, name);
            }
        }
    }
}
