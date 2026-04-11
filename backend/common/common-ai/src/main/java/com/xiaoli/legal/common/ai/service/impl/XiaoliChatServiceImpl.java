package com.xiaoli.legal.common.ai.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.xiaoli.legal.common.ai.config.XiaoliAiProperties;
import com.xiaoli.legal.common.ai.dto.ChatRequest;
import com.xiaoli.legal.common.ai.dto.ChatResponse;
import com.xiaoli.legal.common.ai.service.XiaoliChatService;
import okhttp3.*;
import okio.BufferedSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 小理AI对话服务实现
 */
@Service
public class XiaoliChatServiceImpl implements XiaoliChatService {

    private static final Logger log = LoggerFactory.getLogger(XiaoliChatServiceImpl.class);

    private final OkHttpClient okHttpClient;
    private final XiaoliAiProperties properties;

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String CHAT_API_PATH = "/v1/chat/completions";

    public XiaoliChatServiceImpl(OkHttpClient okHttpClient, XiaoliAiProperties properties) {
        this.okHttpClient = okHttpClient;
        this.properties = properties;
    }

    @Override
    public ChatResponse chat(ChatRequest request) {
        // 设置默认模型
        if (request.getModel() == null) {
            request.setModel(properties.getModel());
        }

        // 构建请求体
        JSONObject jsonBody = buildRequestJson(request);

        RequestBody body = RequestBody.create(jsonBody.toJSONString(), MEDIA_TYPE_JSON);
        Request httpRequest = new Request.Builder()
                .url(properties.getBaseUrl() + CHAT_API_PATH)
                .post(body)
                .build();

        try (Response response = okHttpClient.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                log.error("AI对话请求失败: {}, {}", response.code(), response.message());
                throw new RuntimeException("AI对话请求失败: " + response.code());
            }

            String responseBody = response.body().string();
            return parseChatResponse(responseBody);

        } catch (IOException e) {
            log.error("AI对话请求异常", e);
            throw new RuntimeException("AI对话请求异常: " + e.getMessage());
        }
    }

    @Override
    public String chat(String message) {
        List<ChatRequest.ChatMessage> messages = new ArrayList<>();
        messages.add(ChatRequest.ChatMessage.builder()
                .role("user")
                .content(message)
                .build());
        return chat(messages);
    }

    @Override
    public String chat(String systemPrompt, String message) {
        List<ChatRequest.ChatMessage> messages = new ArrayList<>();
        if (systemPrompt != null) {
            messages.add(ChatRequest.ChatMessage.builder()
                    .role("system")
                    .content(systemPrompt)
                    .build());
        }
        messages.add(ChatRequest.ChatMessage.builder()
                .role("user")
                .content(message)
                .build());
        return chat(messages);
    }

    @Override
    public String chat(List<ChatRequest.ChatMessage> messages) {
        ChatRequest request = ChatRequest.builder()
                .model(properties.getModel())
                .messages(messages)
                .temperature(0.7)
                .maxTokens(2048)
                .stream(false)
                .build();

        ChatResponse response = chat(request);
        return response.getContent();
    }

    @Override
    public void chatStream(ChatRequest request, StreamConsumer consumer) {
        // 设置默认模型和流体
        if (request.getModel() == null) {
            request.setModel(properties.getModel());
        }
        request.setStream(true);

        // 构建请求体
        JSONObject jsonBody = buildRequestJson(request);

        RequestBody body = RequestBody.create(jsonBody.toJSONString(), MEDIA_TYPE_JSON);
        Request httpRequest = new Request.Builder()
                .url(properties.getBaseUrl() + CHAT_API_PATH)
                .post(body)
                .build();

        okHttpClient.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log.error("流式对话请求失败", e);
                consumer.accept("抱歉，发生了错误: " + e.getMessage(), true);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    consumer.accept("抱歉，发生了错误: " + response.code(), true);
                    return;
                }

                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    consumer.accept("", true);
                    return;
                }

                try {
                    String line;
                    BufferedSource source = responseBody.source();
                    while ((line = source.readUtf8Line()) != null) {
                        if (line.startsWith("data: ")) {
                            String data = line.substring(6);
                            if ("[DONE]".equals(data)) {
                                consumer.accept("", true);
                                break;
                            }

                            try {
                                JSONObject json = JSON.parseObject(data);
                                String content = extractContentFromChunk(json);
                                if (content != null) {
                                    consumer.accept(content, false);
                                }
                            } catch (Exception e) {
                                log.debug("解析流式响应异常: {}", data);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("流式响应处理异常", e);
                    consumer.accept("抱歉，处理响应时发生错误", true);
                }
            }
        });
    }

    /**
     * 构建请求JSON
     */
    private JSONObject buildRequestJson(ChatRequest request) {
        JSONObject json = new JSONObject();
        json.put("model", request.getModel());
        json.put("messages", request.getMessages());
        json.put("temperature", request.getTemperature());
        json.put("max_tokens", request.getMaxTokens());
        json.put("stream", request.getStream() != null ? request.getStream() : false);

        if (request.getFrequencyPenalty() != null) {
            json.put("frequency_penalty", request.getFrequencyPenalty());
        }
        if (request.getPresencePenalty() != null) {
            json.put("presence_penalty", request.getPresencePenalty());
        }
        return json;
    }

    /**
     * 解析响应
     */
    private ChatResponse parseChatResponse(String responseBody) {
        JSONObject json = JSON.parseObject(responseBody);

        // 检查错误
        if (json.containsKey("error")) {
            JSONObject error = json.getJSONObject("error");
            throw new RuntimeException(error.getString("message"));
        }

        List<JSONObject> choices = json.getJSONArray("choices").toJavaList(JSONObject.class);
        JSONObject firstChoice = choices.get(0);
        JSONObject message = firstChoice.getJSONObject("message");

        ChatResponse response = ChatResponse.builder()
                .id(json.getString("id"))
                .model(json.getString("model"))
                .created(json.getLong("created"))
                .content(message.getString("content"))
                .finishReason(firstChoice.getString("finish_reason"))
                .index(firstChoice.getIntValue("index"))
                .build();

        if (json.containsKey("usage")) {
            JSONObject usageJson = json.getJSONObject("usage");
            response.setUsage(ChatResponse.Usage.builder()
                    .promptTokens(usageJson.getIntValue("prompt_tokens"))
                    .completionTokens(usageJson.getIntValue("completion_tokens"))
                    .totalTokens(usageJson.getIntValue("total_tokens"))
                    .build());
        }

        return response;
    }

    /**
     * 从流式chunk中提取内容
     */
    private String extractContentFromChunk(JSONObject json) {
        if (!json.containsKey("choices")) {
            return null;
        }
        List<JSONObject> choices = json.getJSONArray("choices").toJavaList(JSONObject.class);
        if (choices.isEmpty()) {
            return null;
        }
        JSONObject choice = choices.get(0);
        if (choice.containsKey("delta") && choice.getJSONObject("delta").containsKey("content")) {
            return choice.getJSONObject("delta").getString("content");
        }
        return null;
    }
}
