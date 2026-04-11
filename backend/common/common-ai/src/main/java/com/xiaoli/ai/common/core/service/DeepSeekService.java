package com.xiaoli.ai.common.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaoli.ai.common.core.config.DeepSeekConfig;
import com.xiaoli.ai.common.core.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * DeepSeek API 服务
 */
@Service
public class DeepSeekService {

    private static final Logger log = LoggerFactory.getLogger(DeepSeekService.class);

    @Autowired
    private DeepSeekConfig deepSeekConfig;

    @Autowired
    private OkHttpClient deepSeekOkHttpClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 分析用户意图
     */
    public Intent analyzeIntent(String question) throws IOException {
        log.info("开始分析用户意图: {}", question);

        DeepSeekRequest.IntentAnalysisRequest request = 
                DeepSeekRequest.createIntentRequest(question, deepSeekConfig.getModel());

        String requestBody = objectMapper.writeValueAsString(request);
        Request httpRequest = new Request.Builder()
                .url(deepSeekConfig.getApiUrl() + "/chat/completions")
                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                .build();

        try (Response response = deepSeekOkHttpClient.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                log.error("DeepSeek API 调用失败: {}", response.code());
                throw new RuntimeException("DeepSeek API 调用失败: " + response.code());
            }

            String responseBody = response.body().string();
            log.info("DeepSeek API 响应: {}", responseBody);

            // 解析响应
            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            JsonNode contentNode = jsonResponse.at("/choices/0/message/content");
            
            if (contentNode == null || contentNode.isNull()) {
                throw new RuntimeException("DeepSeek API 响应格式错误");
            }

            // 解析意图分析结果
            JsonNode intentData = objectMapper.readTree(contentNode.asText());
            
            return Intent.builder()
                    .type(Intent.IntentType.valueOf(intentData.get("type").asText()))
                    .needSearch(intentData.get("need_search").asBoolean())
                    .confidence(intentData.get("confidence").asDouble())
                    .reason(intentData.get("reason").asText())
                    .originalQuestion(question)
                    .build();
        }
    }

    /**
     * 通用对话接口
     */
    public String chat(String content, String systemPrompt) throws IOException {
        log.info("通用对话: {}", content);

        DeepSeekRequest request = DeepSeekRequest.builder()
                .model(deepSeekConfig.getModel())
                .messages(List.of(
                        DeepSeekRequest.Message.builder()
                                .role("system")
                                .content(systemPrompt)
                                .build(),
                        DeepSeekRequest.Message.builder()
                                .role("user")
                                .content(content)
                                .build()
                ))
                .temperature(0.7)
                .maxTokens(2000)
                .stream(false)
                .build();

        String requestBody = objectMapper.writeValueAsString(request);
        Request httpRequest = new Request.Builder()
                .url(deepSeekConfig.getApiUrl() + "/chat/completions")
                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                .build();

        try (Response response = deepSeekOkHttpClient.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                log.error("DeepSeek API 调用失败: {}", response.code());
                throw new RuntimeException("DeepSeek API 调用失败: " + response.code());
            }

            String responseBody = response.body().string();
            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            
            return jsonResponse.at("/choices/0/message/content").asText();
        }
    }

    /**
     * 生成回答（通用场景）
     */
    public String generateAnswer(String question) throws IOException {
        log.info("生成回答: {}", question);

        DeepSeekRequest request = DeepSeekRequest.builder()
                .model(deepSeekConfig.getModel())
                .messages(List.of(
                        DeepSeekRequest.Message.builder()
                                .role("system")
                                .content("你是一个友好的AI助手，请用简洁、准确的语言回答用户的问题。")
                                .build(),
                        DeepSeekRequest.Message.builder()
                                .role("user")
                                .content(question)
                                .build()
                ))
                .temperature(0.7)
                .maxTokens(2000)
                .stream(false)
                .build();

        String requestBody = objectMapper.writeValueAsString(request);
        Request httpRequest = new Request.Builder()
                .url(deepSeekConfig.getApiUrl() + "/chat/completions")
                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                .build();

        try (Response response = deepSeekOkHttpClient.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                log.error("DeepSeek API 调用失败: {}", response.code());
                throw new RuntimeException("DeepSeek API 调用失败: " + response.code());
            }

            String responseBody = response.body().string();
            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            
            return jsonResponse.at("/choices/0/message/content").asText();
        }
    }

    /**
     * 基于检索结果生成专业回答
     */
    public String generateProfessionalAnswer(String question, String searchResults) throws IOException {
        log.info("基于检索结果生成专业回答");

        String systemPrompt = """
                你是一个专业的法律AI助手。用户提出了一个法律问题，你已经通过检索获得了相关的法律案例和法规信息。
                
                请基于以下检索结果，为用户提供专业、严谨、有法律依据的回答。
                
                要求：
                1. 回答要基于检索到的法律条文和案例，引用具体的法条和案例
                2. 说明法律关系的要点和风险点
                3. 提供实用的建议和解决方案
                4. 如果检索结果不足，要明确说明
                5. 保持客观中立，不做确定性过强的判断
                
                检索结果：
                %s
                
                用户问题：
                %s
                """.formatted(searchResults, question);

        DeepSeekRequest request = DeepSeekRequest.builder()
                .model(deepSeekConfig.getModel())
                .messages(List.of(
                        DeepSeekRequest.Message.builder()
                                .role("system")
                                .content(systemPrompt)
                                .build(),
                        DeepSeekRequest.Message.builder()
                                .role("user")
                                .content(question)
                                .build()
                ))
                .temperature(0.3)  // 降低温度，提高准确性
                .maxTokens(3000)
                .stream(false)
                .build();

        String requestBody = objectMapper.writeValueAsString(request);
        Request httpRequest = new Request.Builder()
                .url(deepSeekConfig.getApiUrl() + "/chat/completions")
                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                .build();

        try (Response response = deepSeekOkHttpClient.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                log.error("DeepSeek API 调用失败: {}", response.code());
                throw new RuntimeException("DeepSeek API 调用失败: " + response.code());
            }

            String responseBody = response.body().string();
            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            
            return jsonResponse.at("/choices/0/message/content").asText();
        }
    }
}
