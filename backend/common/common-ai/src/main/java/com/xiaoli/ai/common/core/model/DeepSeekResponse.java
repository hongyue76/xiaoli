package com.xiaoli.ai.common.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DeepSeek API 响应模型
 */
public class DeepSeekResponse {
    
    @JsonProperty("id")
    private String id;

    @JsonProperty("object")
    private String object;

    @JsonProperty("created")
    private Long created;

    @JsonProperty("model")
    private String model;

    @JsonProperty("choices")
    private List<Choice> choices;

    @JsonProperty("usage")
    private Usage usage;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getObject() { return object; }
    public void setObject(String object) { this.object = object; }

    public Long getCreated() { return created; }
    public void setCreated(Long created) { this.created = created; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public List<Choice> getChoices() { return choices; }
    public void setChoices(List<Choice> choices) { this.choices = choices; }

    public Usage getUsage() { return usage; }
    public void setUsage(Usage usage) { this.usage = usage; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id;
        private String object;
        private Long created;
        private String model;
        private List<Choice> choices;
        private Usage usage;

        public Builder id(String id) { this.id = id; return this; }
        public Builder object(String object) { this.object = object; return this; }
        public Builder created(Long created) { this.created = created; return this; }
        public Builder model(String model) { this.model = model; return this; }
        public Builder choices(List<Choice> choices) { this.choices = choices; return this; }
        public Builder usage(Usage usage) { this.usage = usage; return this; }

        public DeepSeekResponse build() {
            DeepSeekResponse resp = new DeepSeekResponse();
            resp.id = this.id;
            resp.object = this.object;
            resp.created = this.created;
            resp.model = this.model;
            resp.choices = this.choices;
            resp.usage = this.usage;
            return resp;
        }
    }

    /**
     * 意图分析响应
     */
    public static class IntentAnalysisResponse {
        @JsonProperty("type")
        private String type;

        @JsonProperty("need_search")
        private Boolean needSearch;

        @JsonProperty("confidence")
        private Double confidence;

        @JsonProperty("reason")
        private String reason;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public Boolean getNeedSearch() { return needSearch; }
        public void setNeedSearch(Boolean needSearch) { this.needSearch = needSearch; }

        public Double getConfidence() { return confidence; }
        public void setConfidence(Double confidence) { this.confidence = confidence; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String type;
            private Boolean needSearch;
            private Double confidence;
            private String reason;

            public Builder type(String type) { this.type = type; return this; }
            public Builder needSearch(Boolean needSearch) { this.needSearch = needSearch; return this; }
            public Builder confidence(Double confidence) { this.confidence = confidence; return this; }
            public Builder reason(String reason) { this.reason = reason; return this; }

            public IntentAnalysisResponse build() {
                IntentAnalysisResponse r = new IntentAnalysisResponse();
                r.type = this.type;
                r.needSearch = this.needSearch;
                r.confidence = this.confidence;
                r.reason = this.reason;
                return r;
            }
        }
    }

    /**
     * 选择对象
     */
    public static class Choice {
        @JsonProperty("index")
        private Integer index;

        @JsonProperty("message")
        private Message message;

        @JsonProperty("finish_reason")
        private String finishReason;

        public Integer getIndex() { return index; }
        public void setIndex(Integer index) { this.index = index; }

        public Message getMessage() { return message; }
        public void setMessage(Message message) { this.message = message; }

        public String getFinishReason() { return finishReason; }
        public void setFinishReason(String finishReason) { this.finishReason = finishReason; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private Integer index;
            private Message message;
            private String finishReason;

            public Builder index(Integer index) { this.index = index; return this; }
            public Builder message(Message message) { this.message = message; return this; }
            public Builder finishReason(String finishReason) { this.finishReason = finishReason; return this; }

            public Choice build() {
                Choice c = new Choice();
                c.index = this.index;
                c.message = this.message;
                c.finishReason = this.finishReason;
                return c;
            }
        }
    }

    /**
     * 消息对象
     */
    public static class Message {
        @JsonProperty("role")
        private String role;

        @JsonProperty("content")
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

            public Message build() {
                Message m = new Message();
                m.role = this.role;
                m.content = this.content;
                return m;
            }
        }
    }

    /**
     * 使用统计
     */
    public static class Usage {
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;

        @JsonProperty("completion_tokens")
        private Integer completionTokens;

        @JsonProperty("total_tokens")
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
