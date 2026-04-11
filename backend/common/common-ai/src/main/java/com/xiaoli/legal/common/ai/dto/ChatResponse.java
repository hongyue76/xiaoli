package com.xiaoli.legal.common.ai.dto;

/**
 * AI对话响应
 */
public class ChatResponse {

    /**
     * 响应ID
     */
    private String id;

    /**
     * 模型
     */
    private String model;

    /**
     * 创建时间
     */
    private Long created;

    /**
     * 回复内容
     */
    private String content;

    /**
     * 完成原因
     */
    private String finishReason;

    /**
     * 用量统计
     */
    private Usage usage;

    /**
     * 索引
     */
    private Integer index;

    /**
     * 流体回调
     */
    private String delta;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Long getCreated() { return created; }
    public void setCreated(Long created) { this.created = created; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getFinishReason() { return finishReason; }
    public void setFinishReason(String finishReason) { this.finishReason = finishReason; }

    public Usage getUsage() { return usage; }
    public void setUsage(Usage usage) { this.usage = usage; }

    public Integer getIndex() { return index; }
    public void setIndex(Integer index) { this.index = index; }

    public String getDelta() { return delta; }
    public void setDelta(String delta) { this.delta = delta; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id;
        private String model;
        private Long created;
        private String content;
        private String finishReason;
        private Usage usage;
        private Integer index;
        private String delta;

        public Builder id(String id) { this.id = id; return this; }
        public Builder model(String model) { this.model = model; return this; }
        public Builder created(Long created) { this.created = created; return this; }
        public Builder content(String content) { this.content = content; return this; }
        public Builder finishReason(String finishReason) { this.finishReason = finishReason; return this; }
        public Builder usage(Usage usage) { this.usage = usage; return this; }
        public Builder index(Integer index) { this.index = index; return this; }
        public Builder delta(String delta) { this.delta = delta; return this; }

        public ChatResponse build() {
            ChatResponse resp = new ChatResponse();
            resp.id = this.id;
            resp.model = this.model;
            resp.created = this.created;
            resp.content = this.content;
            resp.finishReason = this.finishReason;
            resp.usage = this.usage;
            resp.index = this.index;
            resp.delta = this.delta;
            return resp;
        }
    }

    public static class Usage {
        /**
         * 提示词token
         */
        private Integer promptTokens;

        /**
         * 完成token
         */
        private Integer completionTokens;

        /**
         * 总token
         */
        private Integer totalTokens;

        public Integer getPromptTokens() { return promptTokens; }
        public void setPromptTokens(Integer promptTokens) { this.promptTokens = promptTokens; }

        public Integer getCompletionTokens() { return completionTokens; }
        public void setCompletionTokens(Integer completionTokens) { this.completionTokens = completionTokens; }

        public Integer getTotalTokens() { return totalTokens; }
        public void setTotalTokens(Integer totalTokens) { this.totalTokens = totalTokens; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private Integer promptTokens;
            private Integer completionTokens;
            private Integer totalTokens;

            public Builder promptTokens(Integer promptTokens) { this.promptTokens = promptTokens; return this; }
            public Builder completionTokens(Integer completionTokens) { this.completionTokens = completionTokens; return this; }
            public Builder totalTokens(Integer totalTokens) { this.totalTokens = totalTokens; return this; }

            public Usage build() {
                Usage u = new Usage();
                u.promptTokens = this.promptTokens;
                u.completionTokens = this.completionTokens;
                u.totalTokens = this.totalTokens;
                return u;
            }
        }
    }
}
