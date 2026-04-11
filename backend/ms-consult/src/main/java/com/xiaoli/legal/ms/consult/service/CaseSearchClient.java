package com.xiaoli.legal.ms.consult.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaoli.legal.ms.consult.config.CaseSearchConfig;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 案例检索服务客户端
 */
@Service
public class CaseSearchClient {

    private static final Logger log = LoggerFactory.getLogger(CaseSearchClient.class);

    private final CaseSearchConfig caseSearchConfig;
    private final ObjectMapper objectMapper;

    public CaseSearchClient(CaseSearchConfig caseSearchConfig, ObjectMapper objectMapper) {
        this.caseSearchConfig = caseSearchConfig;
        this.objectMapper = objectMapper;
    }

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    /**
     * 语义检索相似案例
     *
     * @param query  查询关键词
     * @param limit  返回数量
     * @return 相似案例列表
     */
    public List<SimilarCase> searchSimilarCases(String query, int limit) {
        if (!caseSearchConfig.isEnabled()) {
            log.debug("案例检索服务未启用");
            return new ArrayList<>();
        }

        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            // 构建请求体
            String jsonBody = String.format(
                    "{\"keyword\":\"%s\",\"caseType\":\"\",\"page\":1,\"size\":%d}",
                    query.replace("\"", "\\\""), limit
            );

            String url = caseSearchConfig.getBaseUrl() + "/api/case/semantic/search";
            RequestBody body = RequestBody.create(jsonBody, JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(caseSearchConfig.getTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(caseSearchConfig.getTimeout(), TimeUnit.MILLISECONDS)
                    .writeTimeout(caseSearchConfig.getTimeout(), TimeUnit.MILLISECONDS)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.warn("案例检索调用失败: {} - {}", response.code(), response.message());
                    return new ArrayList<>();
                }

                String responseBody = response.body() != null ? response.body().string() : "";
                return parseSimilarCases(responseBody);
            }
        } catch (Exception e) {
            log.error("案例检索异常: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 解析相似案例结果
     */
    @SuppressWarnings("unchecked")
    private List<SimilarCase> parseSimilarCases(String responseBody) {
        List<SimilarCase> cases = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode data = root.path("data");
            if (data.isArray()) {
                for (JsonNode node : data) {
                    SimilarCase similarCase = new SimilarCase();
                    similarCase.setId(node.path("id").asLong());
                    similarCase.setTitle(node.path("title").asText());
                    similarCase.setCaseNo(node.path("caseNo").asText());
                    similarCase.setCaseType(node.path("caseType").asText());
                    similarCase.setCourt(node.path("court").asText());
                    similarCase.setJudgmentDate(node.path("judgmentDate").asText());
                    similarCase.setSummary(node.path("summary").asText());
                    similarCase.setJudgmentResult(node.path("judgmentResult").asText());
                    similarCase.setLegalBasis(node.path("legalBasis").asText());
                    similarCase.setScore(node.path("score").asDouble(0.0));
                    cases.add(similarCase);
                }
            }
        } catch (Exception e) {
            log.error("解析相似案例失败: {}", e.getMessage());
        }
        return cases;
    }

    /**
     * 相似案例VO
     */
    public static class SimilarCase {
        private Long id;
        private String title;
        private String caseNo;
        private String caseType;
        private String court;
        private String judgmentDate;
        private String summary;
        private String judgmentResult;
        private String legalBasis;
        private Double score;

        // Getters
        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getCaseNo() { return caseNo; }
        public String getCaseType() { return caseType; }
        public String getCourt() { return court; }
        public String getJudgmentDate() { return judgmentDate; }
        public String getSummary() { return summary; }
        public String getJudgmentResult() { return judgmentResult; }
        public String getLegalBasis() { return legalBasis; }
        public Double getScore() { return score; }

        // Setters
        public void setId(Long id) { this.id = id; }
        public void setTitle(String title) { this.title = title; }
        public void setCaseNo(String caseNo) { this.caseNo = caseNo; }
        public void setCaseType(String caseType) { this.caseType = caseType; }
        public void setCourt(String court) { this.court = court; }
        public void setJudgmentDate(String judgmentDate) { this.judgmentDate = judgmentDate; }
        public void setSummary(String summary) { this.summary = summary; }
        public void setJudgmentResult(String judgmentResult) { this.judgmentResult = judgmentResult; }
        public void setLegalBasis(String legalBasis) { this.legalBasis = legalBasis; }
        public void setScore(Double score) { this.score = score; }
    }
}
