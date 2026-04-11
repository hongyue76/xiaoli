package com.xiaoli.legal.common.ai.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.xiaoli.legal.common.ai.config.XiaoliAiProperties;
import com.xiaoli.legal.common.ai.dto.AsrRequest;
import com.xiaoli.legal.common.ai.dto.AsrResponse;
import com.xiaoli.legal.common.ai.service.AsrService;
import okhttp3.*;
import okio.BufferedSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 语音识别服务实现
 */
@Service
public class AsrServiceImpl implements AsrService {

    private static final Logger log = LoggerFactory.getLogger(AsrServiceImpl.class);

    private final XiaoliAiProperties properties;
    private final OkHttpClient okHttpClient;

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String ASR_API_PATH = "/v1/audio/transcriptions";

    public AsrServiceImpl(XiaoliAiProperties properties, OkHttpClient okHttpClient) {
        this.properties = properties;
        this.okHttpClient = okHttpClient;
    }

    @Override
    public AsrResponse recognize(String audioData, AsrRequest request) {
        // 设置默认值
        if (request.getModel() == null) {
            request.setModel(properties.getAsrModel());
        }

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("model", request.getModel());
        jsonBody.put("format", request.getFormat());
        jsonBody.put("sample_rate", request.getSampleRate());
        jsonBody.put("language", request.getLanguage());
        jsonBody.put("audio", audioData);

        RequestBody body = RequestBody.create(jsonBody.toJSONString(), MEDIA_TYPE_JSON);
        Request httpRequest = new Request.Builder()
                .url(properties.getBaseUrl() + ASR_API_PATH)
                .post(body)
                .build();

        try (Response response = okHttpClient.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                log.error("ASR请求失败: {}, {}", response.code(), response.message());
                return AsrResponse.builder()
                        .text("")
                        .done(true)
                        .error("ASR请求失败: " + response.code())
                        .build();
            }

            String responseBody = response.body().string();
            return parseAsrResponse(responseBody);

        } catch (IOException e) {
            log.error("ASR请求异常", e);
            return AsrResponse.builder()
                    .text("")
                    .done(true)
                    .error("ASR请求异常: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public String recognize(String audioData) {
        AsrRequest request = AsrRequest.builder().build();
        AsrResponse response = recognize(audioData, request);
        return response.getText();
    }

    @Override
    public void recognizeStream(String audioData, Consumer consumer) {
        AsrRequest request = AsrRequest.builder()
                .model(properties.getAsrModel())
                .stream(true)
                .build();

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("model", request.getModel());
        jsonBody.put("format", request.getFormat());
        jsonBody.put("sample_rate", request.getSampleRate());
        jsonBody.put("language", request.getLanguage());
        jsonBody.put("audio", audioData);
        jsonBody.put("stream", true);

        RequestBody body = RequestBody.create(jsonBody.toJSONString(), MEDIA_TYPE_JSON);
        Request httpRequest = new Request.Builder()
                .url(properties.getBaseUrl() + ASR_API_PATH)
                .post(body)
                .build();

        okHttpClient.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log.error("流式ASR请求失败", e);
                consumer.accept(AsrResponse.builder()
                        .text("")
                        .done(true)
                        .error(e.getMessage())
                        .build(), true);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    consumer.accept(AsrResponse.builder()
                            .text("")
                            .done(true)
                            .error("请求失败")
                            .build(), true);
                    return;
                }

                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    consumer.accept(AsrResponse.builder().done(true).build(), true);
                    return;
                }

                try {
                    String line;
                    BufferedSource source = responseBody.source();
                    StringBuilder fullText = new StringBuilder();

                    while ((line = source.readUtf8Line()) != null) {
                        if (line.startsWith("data: ")) {
                            String data = line.substring(6);
                            if ("[DONE]".equals(data)) {
                                break;
                            }

                            try {
                                AsrResponse result = parseAsrResponse(data);
                                if (result.getText() != null) {
                                    fullText.append(result.getText());
                                    consumer.accept(result, false);
                                }
                            } catch (Exception e) {
                                log.debug("解析ASR响应异常: {}", data);
                            }
                        }
                    }

                    consumer.accept(AsrResponse.builder()
                            .text(fullText.toString())
                            .done(true)
                            .build(), true);

                } catch (Exception e) {
                    log.error("流式ASR响应处理异常", e);
                    consumer.accept(AsrResponse.builder()
                            .text("")
                            .done(true)
                            .error(e.getMessage())
                            .build(), true);
                }
            }
        });
    }

    /**
     * 解析ASR响应
     */
    private AsrResponse parseAsrResponse(String responseBody) {
        try {
            JSONObject json = JSONObject.parseObject(responseBody);

            if (json.containsKey("error")) {
                return AsrResponse.builder()
                        .text("")
                        .done(true)
                        .error(json.getString("error"))
                        .build();
            }

            return AsrResponse.builder()
                    .text(json.getString("text"))
                    .confidence(json.getDouble("confidence"))
                    .done(json.getBoolean("done"))
                    .build();

        } catch (Exception e) {
            log.error("解析ASR响应异常: {}", responseBody, e);
            return AsrResponse.builder()
                    .text("")
                    .done(true)
                    .error("解析响应异常")
                    .build();
        }
    }
}
