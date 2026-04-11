package com.xiaoli.legal.common.ai.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xiaoli.legal.common.ai.config.YuanqiProperties;
import com.xiaoli.legal.common.ai.service.YuanqiChatService;
import okhttp3.*;
import okio.BufferedSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 腾讯元器智能体对话服务实现
 */
@Service
public class YuanqiChatServiceImpl implements YuanqiChatService {

    private static final Logger log = LoggerFactory.getLogger(YuanqiChatServiceImpl.class);

    private final OkHttpClient okHttpClient;
    private final YuanqiProperties properties;

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    public YuanqiChatServiceImpl(OkHttpClient okHttpClient, YuanqiProperties properties) {
        this.okHttpClient = okHttpClient;
        this.properties = properties;
    }

    @Override
    public String chat(String message) {
        List<Message> messages = new ArrayList<>();
        messages.add(Message.builder()
                .role("user")
                .content(createTextContent(message))
                .build());
        return chat(messages);
    }

    @Override
    public String chat(String systemPrompt, String message) {
        List<Message> messages = new ArrayList<>();
        if (systemPrompt != null) {
            messages.add(Message.builder()
                    .role("system")
                    .content(createTextContent(systemPrompt))
                    .build());
        }
        messages.add(Message.builder()
                .role("user")
                .content(createTextContent(message))
                .build());
        return chat(messages);
    }

    @Override
    public void chatStream(String message, StreamConsumer consumer) {
        List<Message> messages = new ArrayList<>();
        messages.add(Message.builder()
                .role("user")
                .content(createTextContent(message))
                .build());

        JSONObject jsonBody = buildRequestJson(messages, true);

        RequestBody body = RequestBody.create(jsonBody.toJSONString(), MEDIA_TYPE_JSON);
        Request httpRequest = new Request.Builder()
                .url(properties.getBaseUrl() + "/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + properties.getToken())
                .addHeader("X-Source", "openapi")
                .post(body)
                .build();

        okHttpClient.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log.error("元器流式对话请求失败", e);
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

    private String chat(List<Message> messages) {
        JSONObject jsonBody = buildRequestJson(messages, false);

        RequestBody body = RequestBody.create(jsonBody.toJSONString(), MEDIA_TYPE_JSON);
        Request httpRequest = new Request.Builder()
                .url(properties.getBaseUrl() + "/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + properties.getToken())
                .addHeader("X-Source", "openapi")
                .post(body)
                .build();

        try (Response response = okHttpClient.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                log.error("元器对话请求失败: {}, {}", response.code(), response.message());
                throw new RuntimeException("元器对话请求失败: " + response.code());
            }

            String responseBody = response.body().string();
            return parseChatResponse(responseBody);

        } catch (IOException e) {
            log.error("元器对话请求异常", e);
            throw new RuntimeException("元器对话请求异常: " + e.getMessage());
        }
    }

    private JSONObject buildRequestJson(List<Message> messages, boolean stream) {
        JSONObject json = new JSONObject();
        json.put("assistant_id", properties.getAssistantId());
        json.put("user_id", "user_" + System.currentTimeMillis());
        json.put("stream", stream);

        JSONArray messagesArray = new JSONArray();
        for (Message msg : messages) {
            JSONObject msgJson = new JSONObject();
            msgJson.put("role", msg.getRole());
            msgJson.put("content", msg.getContent());
            messagesArray.add(msgJson);
        }
        json.put("messages", messagesArray);

        return json;
    }

    private List<JSONObject> createTextContent(String text) {
        List<JSONObject> content = new ArrayList<>();
        JSONObject textObj = new JSONObject();
        textObj.put("type", "text");
        textObj.put("text", text);
        content.add(textObj);
        return content;
    }

    private String parseChatResponse(String responseBody) {
        JSONObject json = JSON.parseObject(responseBody);

        // 检查错误
        if (json.containsKey("error")) {
            JSONObject error = json.getJSONObject("error");
            throw new RuntimeException(error.getString("message"));
        }

        if (!json.containsKey("choices")) {
            return responseBody;
        }

        List<JSONObject> choices = json.getJSONArray("choices").toJavaList(JSONObject.class);
        if (choices.isEmpty()) {
            return "";
        }

        JSONObject firstChoice = choices.get(0);
        JSONObject message = firstChoice.getJSONObject("message");

        // 处理content格式（可能是数组或字符串）
        if (message.containsKey("content")) {
            Object content = message.get("content");
            if (content instanceof JSONArray) {
                JSONArray contentArray = (JSONArray) content;
                StringBuilder sb = new StringBuilder();
                for (Object item : contentArray) {
                    if (item instanceof JSONObject) {
                        JSONObject itemObj = (JSONObject) item;
                        if ("text".equals(itemObj.getString("type"))) {
                            sb.append(itemObj.getString("text"));
                        }
                    }
                }
                return sb.toString();
            } else if (content instanceof String) {
                return (String) content;
            }
        }

        return message.getString("content");
    }

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
            Object content = choice.getJSONObject("delta").get("content");
            if (content instanceof JSONArray) {
                JSONArray contentArray = (JSONArray) content;
                StringBuilder sb = new StringBuilder();
                for (Object item : contentArray) {
                    if (item instanceof JSONObject) {
                        JSONObject itemObj = (JSONObject) item;
                        if ("text".equals(itemObj.getString("type"))) {
                            sb.append(itemObj.getString("text"));
                        }
                    }
                }
                return sb.toString();
            }
        }
        return null;
    }

    /**
     * 消息对象
     */
    private static class Message {
        private String role;
        private Object content;

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public Object getContent() { return content; }
        public void setContent(Object content) { this.content = content; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String role;
            private Object content;

            public Builder role(String role) { this.role = role; return this; }
            public Builder content(Object content) { this.content = content; return this; }

            public Message build() {
                Message msg = new Message();
                msg.role = this.role;
                msg.content = this.content;
                return msg;
            }
        }
    }
}
