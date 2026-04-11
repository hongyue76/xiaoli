package com.xiaoli.legal.analysis.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    private final ObjectMapper objectMapper;

    @Value("${case.search.base-url:http://localhost:8083}")
    private String baseUrl;

    @Value("${case.search.timeout:10000}")
    private int timeout;

    @Value("${case.search.enabled:true}")
    private boolean enabled;

    public CaseSearchClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    /**
     * 搜索相关案例
     */
    public List<CaseInfo> searchRelatedCases(String keyword, String caseType, int limit) {
        if (!enabled) {
            log.debug("案例检索服务未启用");
            return new ArrayList<>();
        }

        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            String jsonBody = String.format(
                    "{\"keyword\":\"%s\",\"caseType\":\"%s\",\"page\":1,\"size\":%d}",
                    keyword.replace("\"", "\\\""),
                    caseType != null ? caseType : "",
                    limit
            );

            String url = baseUrl + "/api/case/semantic/search";
            RequestBody body = RequestBody.create(jsonBody, JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                    .readTimeout(timeout, TimeUnit.MILLISECONDS)
                    .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.warn("案例检索调用失败: {} - {}", response.code(), response.message());
                    return new ArrayList<>();
                }

                String responseBody = response.body() != null ? response.body().string() : "";
                return parseCases(responseBody);
            }
        } catch (Exception e) {
            log.error("案例检索异常: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<CaseInfo> parseCases(String responseBody) {
        List<CaseInfo> cases = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode data = root.path("data");
            if (data.isArray()) {
                for (JsonNode node : data) {
                    CaseInfo caseInfo = new CaseInfo();
                    caseInfo.setId(node.path("id").asLong());
                    caseInfo.setTitle(node.path("title").asText());
                    caseInfo.setCaseNo(node.path("caseNo").asText());
                    caseInfo.setCaseType(node.path("caseType").asText());
                    caseInfo.setCourt(node.path("court").asText());
                    caseInfo.setJudgmentDate(node.path("judgmentDate").asText());
                    caseInfo.setSummary(node.path("summary").asText());
                    caseInfo.setJudgmentResult(node.path("judgmentResult").asText());
                    caseInfo.setLegalBasis(node.path("legalBasis").asText());
                    caseInfo.setScore(node.path("score").asDouble(0.0));
                    cases.add(caseInfo);
                }
            }
        } catch (Exception e) {
            log.error("解析案例失败: {}", e.getMessage());
        }
        return cases;
    }

    /**
     * 案例信息
     */
    public static class CaseInfo {
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

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getCaseNo() { return caseNo; }
        public void setCaseNo(String caseNo) { this.caseNo = caseNo; }
        public String getCaseType() { return caseType; }
        public void setCaseType(String caseType) { this.caseType = caseType; }
        public String getCourt() { return court; }
        public void setCourt(String court) { this.court = court; }
        public String getJudgmentDate() { return judgmentDate; }
        public void setJudgmentDate(String judgmentDate) { this.judgmentDate = judgmentDate; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public String getJudgmentResult() { return judgmentResult; }
        public void setJudgmentResult(String judgmentResult) { this.judgmentResult = judgmentResult; }
        public String getLegalBasis() { return legalBasis; }
        public void setLegalBasis(String legalBasis) { this.legalBasis = legalBasis; }
        public Double getScore() { return score; }
        public void setScore(Double score) { this.score = score; }
    }
}
