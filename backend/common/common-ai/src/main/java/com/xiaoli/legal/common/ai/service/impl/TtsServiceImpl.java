package com.xiaoli.legal.common.ai.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.xiaoli.legal.common.ai.config.XiaoliAiProperties;
import com.xiaoli.legal.common.ai.dto.TtsRequest;
import com.xiaoli.legal.common.ai.service.TtsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;

/**
 * 语音合成服务实现
 */
@Service
public class TtsServiceImpl implements TtsService {

    private static final Logger log = LoggerFactory.getLogger(TtsServiceImpl.class);

    private final XiaoliAiProperties properties;
    private final OkHttpClient okHttpClient;

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TTS_API_PATH = "/v1/audio/speech";

    public TtsServiceImpl(XiaoliAiProperties properties, OkHttpClient okHttpClient) {
        this.properties = properties;
        this.okHttpClient = okHttpClient;
    }

    @Override
    public String speech(TtsRequest request) {
        // 璁剧疆榛樿鍊?
        if (request.getModel() == null) {
            request.setModel(properties.getTtsModel());
        }

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("model", request.getModel());
        jsonBody.put("input", request.getInput());
        jsonBody.put("voice", request.getVoice());
        jsonBody.put("speed", request.getSpeed());
        jsonBody.put("volume", request.getVolume());
        jsonBody.put("format", request.getFormat());

        RequestBody body = RequestBody.create(jsonBody.toJSONString(), MEDIA_TYPE_JSON);
        Request httpRequest = new Request.Builder()
                .url(properties.getBaseUrl() + TTS_API_PATH)
                .post(body)
                .build();

        try (Response response = okHttpClient.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                log.error("TTS璇锋眰澶辫触: {}, {}", response.code(), response.message());
                throw new RuntimeException("TTS璇锋眰澶辫触: " + response.code());
            }

            // 杩斿洖浜岃繘鍒堕煶棰戞暟鎹浆鎹负Base64
            byte[] audioData = response.body().bytes();
            return Base64.getEncoder().encodeToString(audioData);

        } catch (IOException e) {
            log.error("TTS璇锋眰寮傚父", e);
            throw new RuntimeException("TTS璇锋眰寮傚父: " + e.getMessage());
        }
    }

    @Override
    public String speech(String text) {
        TtsRequest request = TtsRequest.builder()
                .input(text)
                .build();
        return speech(request);
    }

    @Override
    public void speechStream(String text, Consumer consumer) {
        TtsRequest request = TtsRequest.builder()
                .model(properties.getTtsModel())
                .input(text)
                .build();

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("model", request.getModel());
        jsonBody.put("input", request.getInput());
        jsonBody.put("voice", request.getVoice());
        jsonBody.put("speed", request.getSpeed());
        jsonBody.put("stream", true);

        RequestBody body = RequestBody.create(jsonBody.toJSONString(), MEDIA_TYPE_JSON);
        Request httpRequest = new Request.Builder()
                .url(properties.getBaseUrl() + TTS_API_PATH)
                .post(body)
                .build();

        okHttpClient.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log.error("娴佸紡TTS璇锋眰澶辫触", e);
                consumer.accept(null, true);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    consumer.accept(null, true);
                    return;
                }

                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    consumer.accept(null, true);
                    return;
                }

                try {
                    byte[] audioData = responseBody.bytes();
                    String base64Audio = Base64.getEncoder().encodeToString(audioData);
                    consumer.accept(base64Audio, true);
                } catch (Exception e) {
                    log.error("娴佸紡TTS鍝嶅簲澶勭悊寮傚父", e);
                    consumer.accept(null, true);
                }
            }
        });
    }
}
