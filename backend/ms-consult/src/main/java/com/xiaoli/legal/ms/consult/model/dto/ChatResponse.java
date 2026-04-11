package com.xiaoli.legal.ms.consult.model.dto;

import java.util.List;
import java.util.Map;

/**
 * 小理AI对话响应
 */
public class ChatResponse {

    /**
     * 请求ID
     */
    private String id;

    /**
     * 对象类型
     */
    private String object;

    /**
     * 创建时间戳
     */
    private Long created;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 选择列表
     */
    private List<Choice> choices;

    /**
     * 使用统计
     */
    private Usage usage;

    /**
     * 错误信息
     */
    private Map<String, Object> error;

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

    public Map<String, Object> getError() { return error; }
    public void setError(Map<String, Object> error) { this.error = error; }

    /**
     * 获取回复内容
     */
    public String getContent() {
        if (choices != null && !choices.isEmpty()) {
            Choice choice = choices.get(0);
            if (choice.getMessage() != null) {
                return choice.getMessage().getContent();
            }
        }
        return null;
    }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id;
        private String object;
        private Long created;
        private String model;
        private List<Choice> choices;
        private Usage usage;
        private Map<String, Object> error;

        public Builder id(String id) { this.id = id; return this; }
        public Builder object(String object) { this.object = object; return this; }
        public Builder created(Long created) { this.created = created; return this; }
        public Builder model(String model) { this.model = model; return this; }
        public Builder choices(List<Choice> choices) { this.choices = choices; return this; }
        public Builder usage(Usage usage) { this.usage = usage; return this; }
        public Builder error(Map<String, Object> error) { this.error = error; return this; }

        public ChatResponse build() {
            ChatResponse response = new ChatResponse();
            response.setId(id);
            response.setObject(object);
            response.setCreated(created);
            response.setModel(model);
            response.setChoices(choices);
            response.setUsage(usage);
            response.setError(error);
            return response;
        }
    }

    /**
     * 选择项
     */
    public static class Choice {
        /**
         * 索引
         */
        private Integer index;

        /**
         * 消息
         */
        private ChatRequest.ChatMessage message;

        /**
         * 完成原因
         */
        private String finishReason;

        public Choice() {}

        public Choice(Integer index, ChatRequest.ChatMessage message, String finishReason) {
            this.index = index;
            this.message = message;
            this.finishReason = finishReason;
        }

        // Getters and Setters
        public Integer getIndex() { return index; }
        public void setIndex(Integer index) { this.index = index; }

        public ChatRequest.ChatMessage getMessage() { return message; }
        public void setMessage(ChatRequest.ChatMessage message) { this.message = message; }

        public String getFinishReason() { return finishReason; }
        public void setFinishReason(String finishReason) { this.finishReason = finishReason; }

        // Builder
        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private Integer index;
            private ChatRequest.ChatMessage message;
            private String finishReason;

            public Builder index(Integer index) { this.index = index; return this; }
            public Builder message(ChatRequest.ChatMessage message) { this.message = message; return this; }
            public Builder finishReason(String finishReason) { this.finishReason = finishReason; return this; }

            public Choice build() {
                return new Choice(index, message, finishReason);
            }
        }
    }

    /**
     * 使用统计
     */
    public static class Usage {
        /**
         * 提示token数
         */
        private Integer promptTokens;

        /**
         * 生成token数
         */
        private Integer completionTokens;

        /**
         * 总token数
         */
        private Integer totalTokens;

        public Usage() {}

        public Usage(Integer promptTokens, Integer completionTokens, Integer totalTokens) {
            this.promptTokens = promptTokens;
            this.completionTokens = completionTokens;
            this.totalTokens = totalTokens;
        }

        // Getters and Setters
        public Integer getPromptTokens() { return promptTokens; }
        public void setPromptTokens(Integer promptTokens) { this.promptTokens = promptTokens; }

        public Integer getCompletionTokens() { return completionTokens; }
        public void setCompletionTokens(Integer completionTokens) { this.completionTokens = completionTokens; }

        public Integer getTotalTokens() { return totalTokens; }
        public void setTotalTokens(Integer totalTokens) { this.totalTokens = totalTokens; }

        // Builder
        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private Integer promptTokens;
            private Integer completionTokens;
            private Integer totalTokens;

            public Builder promptTokens(Integer promptTokens) { this.promptTokens = promptTokens; return this; }
            public Builder completionTokens(Integer completionTokens) { this.completionTokens = completionTokens; return this; }
            public Builder totalTokens(Integer totalTokens) { this.totalTokens = totalTokens; return this; }

            public Usage build() {
                return new Usage(promptTokens, completionTokens, totalTokens);
            }
        }
    }
}
