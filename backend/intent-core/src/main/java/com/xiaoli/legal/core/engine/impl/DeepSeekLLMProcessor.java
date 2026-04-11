package com.xiaoli.legal.core.engine.impl;

import com.xiaoli.legal.core.IntentType;
import com.xiaoli.legal.core.engine.LLMEngineProcessor;
import com.xiaoli.legal.core.model.IntentResult;
import com.xiaoli.legal.core.model.UserQuery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * DeepSeek LLM引擎处理器
 */
@Component
public class DeepSeekLLMProcessor implements LLMEngineProcessor {

    @Value("${deepseek.api.key:sk-test-free-key}")
    private String apiKey;

    @Value("${deepseek.api.url:https://api.deepseek.com/v1/chat/completions}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String process(UserQuery query, IntentResult intentResult) {
        String systemPrompt = buildSystemPrompt(intentResult.getIntent());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // 构建请求体
        DeepSeekRequest request = DeepSeekRequest.builder()
            .model("deepseek-chat")
            .messages(new DeepSeekMessage[]{
                new DeepSeekMessage("system", systemPrompt),
                new DeepSeekMessage("user", query.getQueryText())
            })
            .temperature(0.7)
            .max_tokens(2000)
            .build();

        HttpEntity<DeepSeekRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<DeepSeekResponse> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                DeepSeekResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().choices[0].message.content;
            } else {
                throw new RuntimeException("LLM API调用失败");
            }
        } catch (Exception e) {
            return "抱歉，AI大模型暂时无法响应，请稍后重试或切换到规则引擎模式。\n\n" +
                "错误信息：" + e.getMessage();
        }
    }

    @Override
    public void processStream(UserQuery query, IntentResult intentResult, StreamCallback callback) {
        // 流式处理实现（可选）
        String response = process(query, intentResult);
        callback.onComplete(response);
    }

    /**
     * 构建系统提示词
     */
    private String buildSystemPrompt(IntentType intent) {
        String basePrompt = "你是一个专业的法律AI助手，擅长解答各类法律问题。" +
            "请用专业、准确、易懂的语言回答用户的问题。";

        switch (intent) {
            case CONSULT:
                return basePrompt + "\n\n你是法律咨询专家。请按照以下结构回答：\n" +
                    "1. 问题分析\n2. 法律依据\n3. 解决方案\n4. 注意事项";
            case CASE_SEARCH:
                return basePrompt + "\n\n你是案例检索专家。请分析用户的查询，" +
                    "提供相关的法律案例和判例，包括案号、案情和裁判要点。";
            case DOCUMENT:
                return basePrompt + "\n\n你是法律文书专家。请根据用户需求生成专业的法律文书，" +
                    "包括合同书、起诉书、授权委托书等。";
            case CONTRACT_REVIEW:
                return basePrompt + "\n\n你是合同审查专家。请从完整性、合法性、公平性三个维度" +
                    "审查合同，指出风险点并提供修改建议。";
            case CASE_ANALYSIS:
                return basePrompt + "\n\n你是案件分析专家。请从事实认定、法律适用、" +
                    "争议焦点、诉讼策略等方面分析案件。";
            case EVIDENCE_ANALYSIS:
                return basePrompt + "\n\n你是证据分析专家。请分析证据的真实性、合法性、关联性，" +
                    "评估证明力，提供质证意见。";
            case DECISION:
                return basePrompt + "\n\n你是司法决策专家。请提供量刑建议、审判预测，" +
                    "基于类似案例进行风险评估。";
            case COMPLIANCE:
                return basePrompt + "\n\n你是合规检查专家。请从企业合规角度进行审查，" +
                    "识别风险点，提供整改建议。";
            default:
                return basePrompt;
        }
    }

    /**
     * DeepSeek请求对象
     */
    static class DeepSeekRequest {
        private String model;
        private DeepSeekMessage[] messages;
        private double temperature;
        private int max_tokens;

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public DeepSeekMessage[] getMessages() { return messages; }
        public void setMessages(DeepSeekMessage[] messages) { this.messages = messages; }
        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }
        public int getMax_tokens() { return max_tokens; }
        public void setMax_tokens(int max_tokens) { this.max_tokens = max_tokens; }

        public DeepSeekRequest() {}
        public DeepSeekRequest(String model, DeepSeekMessage[] messages, double temperature, int max_tokens) {
            this.model = model;
            this.messages = messages;
            this.temperature = temperature;
            this.max_tokens = max_tokens;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String model;
            private DeepSeekMessage[] messages;
            private double temperature;
            private int max_tokens;

            public Builder model(String model) { this.model = model; return this; }
            public Builder messages(DeepSeekMessage[] messages) { this.messages = messages; return this; }
            public Builder temperature(double temperature) { this.temperature = temperature; return this; }
            public Builder max_tokens(int max_tokens) { this.max_tokens = max_tokens; return this; }

            public DeepSeekRequest build() {
                return new DeepSeekRequest(model, messages, temperature, max_tokens);
            }
        }
    }

    /**
     * DeepSeek消息对象
     */
    static class DeepSeekMessage {
        private String role;
        private String content;

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public DeepSeekMessage() {}
        public DeepSeekMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    /**
     * DeepSeek响应对象
     */
    static class DeepSeekResponse {
        private Choice[] choices;

        public Choice[] getChoices() { return choices; }
        public void setChoices(Choice[] choices) { this.choices = choices; }
    }

    static class Choice {
        private Message message;

        public Message getMessage() { return message; }
        public void setMessage(Message message) { this.message = message; }
    }

    static class Message {
        private String content;

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}
