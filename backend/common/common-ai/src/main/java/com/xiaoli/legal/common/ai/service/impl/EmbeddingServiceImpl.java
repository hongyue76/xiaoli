package com.xiaoli.legal.common.ai.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.xiaoli.legal.common.ai.config.XiaoliAiProperties;
import com.xiaoli.legal.common.ai.dto.EmbeddingRequest;
import com.xiaoli.legal.common.ai.dto.EmbeddingResponse;
import com.xiaoli.legal.common.ai.service.EmbeddingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 向量嵌入服务实现
 */
@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingServiceImpl.class);

    private final XiaoliAiProperties properties;
    private final okhttp3.OkHttpClient okHttpClient;

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String EMBEDDING_API_PATH = "/v1/embeddings";

    public EmbeddingServiceImpl(XiaoliAiProperties properties, okhttp3.OkHttpClient okHttpClient) {
        this.properties = properties;
        this.okHttpClient = okHttpClient;
    }

    @Override
    public float[] embed(String text) {
        EmbeddingRequest request = EmbeddingRequest.builder()
                .model(properties.getEmbeddingModel())
                .input(text)
                .build();

        try {
            String responseBody = doRequest(request);
            return parseEmbeddingResponse(responseBody);
        } catch (Exception e) {
            log.error("鍚戦噺鍖栧け璐? {}", text, e);
            throw new RuntimeException("鍚戦噺鍖栧け璐? " + e.getMessage());
        }
    }

    @Override
    public List<float[]> embed(List<String> texts) {
        EmbeddingRequest request = EmbeddingRequest.builder()
                .model(properties.getEmbeddingModel())
                .inputs(texts.toArray(new String[0]))
                .build();

        try {
            String responseBody = doRequest(request);
            return parseEmbeddingsResponse(responseBody);
        } catch (Exception e) {
            log.error("批量向量初始化失败", e);
            throw new RuntimeException("批量向量初始化失败: " + e.getMessage());
        }
    }

    @Override
    public int getDimension() {
        // 榛樿鍚戦噺缁村害
        return 1536;
    }

    /**
     * 鎵ц璇锋眰
     */
    private String doRequest(EmbeddingRequest request) throws IOException {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("model", request.getModel());

        if (request.getInput() != null) {
            jsonBody.put("input", request.getInput());
        }
        if (request.getInputs() != null) {
            jsonBody.put("input", request.getInputs());
        }

        RequestBody body = RequestBody.create(jsonBody.toJSONString(), MEDIA_TYPE_JSON);
        Request httpRequest = new Request.Builder()
                .url(properties.getBaseUrl() + EMBEDDING_API_PATH)
                .post(body)
                .build();

        try (Response response = okHttpClient.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("鍚戦噺鍖栬姹傚け璐? " + response.code());
            }
            return response.body().string();
        }
    }

    /**
     * 瑙ｆ瀽鍗曟潯鍝嶅簲
     */
    private float[] parseEmbeddingResponse(String responseBody) {
        JSONObject json = JSON.parseObject(responseBody);

        if (json.containsKey("error")) {
            JSONObject error = json.getJSONObject("error");
            throw new RuntimeException(error.getString("message"));
        }

        List<JSONObject> data = json.getJSONArray("data").toJavaList(JSONObject.class);
        if (data.isEmpty()) {
            throw new RuntimeException("鏈幏鍙栧埌鍚戦噺鏁版嵁");
        }

        String embeddingStr = data.get(0).getString("embedding");
        // 瑙ｆ瀽瀛楃涓叉牸寮忕殑鍚戦噺
        String[] parts = embeddingStr.substring(1, embeddingStr.length() - 1).split(",");
        float[] embedding = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            embedding[i] = Float.parseFloat(parts[i].trim());
        }
        return embedding;
    }

    /**
     * 瑙ｆ瀽鎵归噺鍝嶅簲
     */
    private List<float[]> parseEmbeddingsResponse(String responseBody) {
        JSONObject json = JSON.parseObject(responseBody);

        if (json.containsKey("error")) {
            JSONObject error = json.getJSONObject("error");
            throw new RuntimeException(error.getString("message"));
        }

        List<JSONObject> data = json.getJSONArray("data").toJavaList(JSONObject.class);
        List<float[]> embeddings = new ArrayList<>(data.size());

        for (JSONObject item : data) {
            String embeddingStr = item.getString("embedding");
            String[] parts = embeddingStr.substring(1, embeddingStr.length() - 1).split(",");
            float[] embedding = new float[parts.length];
            for (int i = 0; i < parts.length; i++) {
                embedding[i] = Float.parseFloat(parts[i].trim());
            }
            embeddings.add(embedding);
        }

        return embeddings;
    }
}
