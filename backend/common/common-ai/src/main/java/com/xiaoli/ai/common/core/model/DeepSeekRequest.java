package com.xiaoli.ai.common.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DeepSeek API 请求模型
 */
public class DeepSeekRequest {
    
    @JsonProperty("model")
    private String model;

    @JsonProperty("messages")
    private List<Message> messages;

    @JsonProperty("temperature")
    private Double temperature;

    @JsonProperty("max_tokens")
    private Integer maxTokens;

    @JsonProperty("stream")
    private Boolean stream;

    // Getters and Setters
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }

    public Boolean getStream() { return stream; }
    public void setStream(Boolean stream) { this.stream = stream; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String model;
        private List<Message> messages;
        private Double temperature;
        private Integer maxTokens;
        private Boolean stream;

        public Builder model(String model) { this.model = model; return this; }
        public Builder messages(List<Message> messages) { this.messages = messages; return this; }
        public Builder temperature(Double temperature) { this.temperature = temperature; return this; }
        public Builder maxTokens(Integer maxTokens) { this.maxTokens = maxTokens; return this; }
        public Builder stream(Boolean stream) { this.stream = stream; return this; }

        public DeepSeekRequest build() {
            DeepSeekRequest req = new DeepSeekRequest();
            req.model = this.model;
            req.messages = this.messages;
            req.temperature = this.temperature;
            req.maxTokens = this.maxTokens;
            req.stream = this.stream;
            return req;
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
                Message msg = new Message();
                msg.role = this.role;
                msg.content = this.content;
                return msg;
            }
        }
    }

    /**
     * 意图判断的专用请求
     */
    public static class IntentAnalysisRequest {
        @JsonProperty("model")
        private String model;

        @JsonProperty("messages")
        private List<Message> messages;

        @JsonProperty("temperature")
        private Double temperature;

        @JsonProperty("response_format")
        private ResponseFormat responseFormat;

        // Getters and Setters
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }

        public List<Message> getMessages() { return messages; }
        public void setMessages(List<Message> messages) { this.messages = messages; }

        public Double getTemperature() { return temperature; }
        public void setTemperature(Double temperature) { this.temperature = temperature; }

        public ResponseFormat getResponseFormat() { return responseFormat; }
        public void setResponseFormat(ResponseFormat responseFormat) { this.responseFormat = responseFormat; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String model;
            private List<Message> messages;
            private Double temperature;
            private ResponseFormat responseFormat;

            public Builder model(String model) { this.model = model; return this; }
            public Builder messages(List<Message> messages) { this.messages = messages; return this; }
            public Builder temperature(Double temperature) { this.temperature = temperature; return this; }
            public Builder responseFormat(ResponseFormat responseFormat) { this.responseFormat = responseFormat; return this; }

            public IntentAnalysisRequest build() {
                IntentAnalysisRequest req = new IntentAnalysisRequest();
                req.model = this.model;
                req.messages = this.messages;
                req.temperature = this.temperature;
                req.responseFormat = this.responseFormat;
                return req;
            }
        }

        /**
         * JSON 格式响应
         */
        public static class ResponseFormat {
            @JsonProperty("type")
            private String type;

            public String getType() { return type; }
            public void setType(String type) { this.type = type; }

            public static Builder builder() { return new Builder(); }

            public static class Builder {
                private String type;

                public Builder type(String type) { this.type = type; return this; }

                public ResponseFormat build() {
                    ResponseFormat rf = new ResponseFormat();
                    rf.type = this.type;
                    return rf;
                }
            }
        }
    }

    /**
     * 创建意图判断请求
     */
    public static IntentAnalysisRequest createIntentRequest(String question, String modelName) {
        return IntentAnalysisRequest.builder()
                .model(modelName)
                .messages(List.of(
                        Message.builder()
                                .role("system")
                                .content(INTENT_SYSTEM_PROMPT)
                                .build(),
                        Message.builder()
                                .role("user")
                                .content(question)
                                .build()
                ))
                .temperature(0.1)
                .responseFormat(IntentAnalysisRequest.ResponseFormat.builder()
                        .type("json_object")
                        .build())
                .build();
    }

    /**
     * 意图识别的系统提示词
     */
    private static final String INTENT_SYSTEM_PROMPT = """
            你是一个智能法律意图识别助手。请分析用户的问题，判断其意图类型。
            
            请严格按照以下JSON格式返回，不要添加任何额外说明：
            {
              "type": "PROFESSIONAL" | "GENERAL" | "CHAT" | "WEATHER" | "SUMMARY" | "AMBIGUOUS",
              "need_search": true | false,
              "confidence": 0.0-1.0,
              "reason": "简短说明判断原因"
            }
            
            意图类型说明：
            - PROFESSIONAL: 专业法律问题，涉及具体法律条文、案例分析、法规检索、合同审查等
            - GENERAL: 通用问题，如常识性提问、一般性咨询
            - CHAT: 日常闲聊，问候、打招呼、日常对话
            - WEATHER: 天气、时间、日期等查询
            - SUMMARY: 内容总结、摘要生成
            - AMBIGUOUS: 意图不明确，需要进一步确认
            
            判断标准：
            1. 如果问题包含法律相关关键词（如：民法典、刑法、合同法、诉讼、赔偿、起诉、仲裁、合规等），且涉及具体场景，判断为 PROFESSIONAL
            2. 如果是日常问候、闲聊，判断为 CHAT
            3. 如果询问天气、时间等客观信息，判断为 WEATHER
            4. 如果要求总结、提炼内容，判断为 SUMMARY
            5. 如果是常识性问题或非法律领域，判断为 GENERAL
            6. 如果问题太模糊或无法判断，判断为 AMBIGUOUS
            
            示例：
            "劳动合同纠纷怎么处理？" -> PROFESSIONAL
            "你好" -> CHAT
            "今天天气怎么样？" -> WEATHER
            "帮我总结这段话" -> SUMMARY
            "生命的意义是什么？" -> GENERAL
            "怎么办？" -> AMBIGUOUS
            
            请确保返回的是纯JSON格式，不要有markdown或其他格式。
            """;
}
