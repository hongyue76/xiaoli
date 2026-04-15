package com.xiaoli.legal.common.ai.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xiaoli.legal.common.ai.config.DelilegalProperties;
import com.xiaoli.legal.common.ai.service.DelilegalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * 得理法搜案例检索服务实现
 */
@Service
public class DelilegalServiceImpl implements DelilegalService {

    private static final Logger log = LoggerFactory.getLogger(DelilegalServiceImpl.class);

    private final OkHttpClient okHttpClient;
    private final DelilegalProperties properties;

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    
    // 并发线程池，用于批量请求
    private final ExecutorService executorService = Executors.newFixedThreadPool(
        Math.min(10, Runtime.getRuntime().availableProcessors() * 2),
        new ThreadFactory() {
            private int count = 1;
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "delilegal-pool-" + count++);
                t.setDaemon(true);
                return t;
            }
        }
    );

    public DelilegalServiceImpl(@Qualifier("okHttpClient") OkHttpClient okHttpClient, DelilegalProperties properties) {
        this.okHttpClient = okHttpClient;
        this.properties = properties;
    }

    @Override
    public String searchCases(List<String> keywords, String longText,
                             List<String> courtLevelArr, List<String> judgementTypeArr) {
        JSONObject jsonBody = new JSONObject();
        
        // 分页参数
        jsonBody.put("pageNo", 1);
        jsonBody.put("pageSize", 5);
        jsonBody.put("sortField", "correlation");
        jsonBody.put("sortOrder", "desc");

        // 构建 condition 对象
        JSONObject condition = new JSONObject();

        // keywordArr 需要是数组
        if (keywords != null && !keywords.isEmpty()) {
            JSONArray keywordArr = new JSONArray();
            for (String keyword : keywords) {
                keywordArr.add(keyword);
            }
            condition.put("keywordArr", keywordArr);
        }

        // longText 语义检索
        if (longText != null && !longText.isEmpty()) {
            condition.put("longText", longText);
        }

        // 法院层级筛选
        if (courtLevelArr != null && !courtLevelArr.isEmpty()) {
            JSONArray courtArr = new JSONArray();
            for (String level : courtLevelArr) {
                courtArr.add(level);
            }
            condition.put("courtLevelArr", courtArr);
        }

        // 文书类型筛选
        if (judgementTypeArr != null && !judgementTypeArr.isEmpty()) {
            JSONArray typeArr = new JSONArray();
            for (String type : judgementTypeArr) {
                typeArr.add(type);
            }
            condition.put("judgementTypeArr", typeArr);
        }
        
        jsonBody.put("condition", condition);

        String url = properties.getBaseUrl() + "/api/qa/v3/search/queryListCase";
        log.info("得理法搜案例检索请求: {}", jsonBody.toJSONString());
        return post(url, jsonBody);
    }

    @Override
    public String searchLaws(String keywords, String fieldName) {
        JSONObject jsonBody = new JSONObject();
        
        // 分页参数
        jsonBody.put("pageNo", 1);
        jsonBody.put("pageSize", 5);
        
        // 构建 condition 对象
        JSONObject condition = new JSONObject();

        // keywords 需要是数组
        if (keywords != null && !keywords.isEmpty()) {
            JSONArray keywordsArr = new JSONArray();
            if (keywords.startsWith("[")) {
                // 解析 JSON 数组字符串
                keywordsArr = JSON.parseArray(keywords);
            } else {
                keywordsArr.add(keywords);
            }
            condition.put("keywords", keywordsArr);
        }

        if (fieldName != null && !fieldName.isEmpty()) {
            condition.put("fieldName", fieldName);
        } else {
            condition.put("fieldName", "semantic"); // 默认语义检索
        }
        
        jsonBody.put("condition", condition);

        String url = properties.getBaseUrl() + "/api/qa/v3/search/queryListLaw";
        log.info("得理法搜法规检索请求: {}", jsonBody.toJSONString());
        return post(url, jsonBody);
    }

    /**
     * 获取法规详情
     * @param lawId 法规ID
     * @param merge 是否合并内容（true: 返回完整正文）
     * @return 法规详情JSON
     */
    @Override
    public String getLawDetail(String lawId, boolean merge) {
        if (lawId == null || lawId.isEmpty()) {
            throw new IllegalArgumentException("lawId不能为空");
        }
        
        String url = properties.getBaseUrl() + "/api/qa/v3/search/lawInfo" 
                   + "?lawId=" + lawId 
                   + "&merge=" + merge;
        
        log.info("得理法搜法规详情请求: lawId={}, merge={}", lawId, merge);
        return get(url);
    }

    /**
     * 批量获取法规详情（并发优化）
     * @param lawIds 法规ID列表
     * @param merge 是否合并内容
     * @return 法规详情列表JSON，失败项标记 fullContent: null
     */
    @Override
    public String getBatchLawDetails(List<String> lawIds, boolean merge) {
        if (lawIds == null || lawIds.isEmpty()) {
            return JSON.toJSONString(Collections.emptyList());
        }
        
        log.info("得理法搜批量法规详情请求: lawIds数量={}, merge={}", lawIds.size(), merge);
        
        // 使用 CompletableFuture 进行并发请求
        List<CompletableFuture<JSONObject>> futures = new ArrayList<>();
        
        for (String lawId : lawIds) {
            CompletableFuture<JSONObject> future = CompletableFuture.supplyAsync(() -> {
                JSONObject result = new JSONObject();
                result.put("lawId", lawId);
                
                try {
                    String detailJson = getLawDetail(lawId, merge);
                    JSONObject detail = JSON.parseObject(detailJson);
                    
                    // 提取 lawDetailContent 字段
                    if (detail != null) {
                        String fullContent = detail.getString("lawDetailContent");
                        if (fullContent != null && fullContent.length() > 500) {
                            // 截取前500字符作为预览，并标记有完整内容
                            result.put("fullContentPreview", fullContent.substring(0, 500) + "...");
                        } else {
                            result.put("fullContentPreview", fullContent);
                        }
                        result.put("fullContent", fullContent);
                        result.put("success", true);
                        
                        // 保留原始详情数据
                        result.put("detail", detail);
                    } else {
                        result.put("fullContent", null);
                        result.put("fullContentPreview", null);
                        result.put("success", false);
                        result.put("error", "未返回有效数据");
                    }
                } catch (Exception e) {
                    log.warn("获取法规详情失败: lawId={}, error={}", lawId, e.getMessage());
                    result.put("fullContent", null);
                    result.put("fullContentPreview", null);
                    result.put("success", false);
                    result.put("error", e.getMessage());
                }
                
                return result;
            }, executorService);
            
            futures.add(future);
        }
        
        // 等待所有请求完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );
        
        try {
            allFutures.get(30, TimeUnit.SECONDS); // 最多等待30秒
        } catch (Exception e) {
            log.error("批量获取法规详情超时或异常", e);
        }
        
        // 收集结果
        List<JSONObject> results = new ArrayList<>();
        for (CompletableFuture<JSONObject> future : futures) {
            if (future.isDone() && !future.isCompletedExceptionally()) {
                try {
                    results.add(future.get());
                } catch (Exception e) {
                    log.error("获取future结果失败", e);
                }
            }
        }
        
        log.info("得理法搜批量法规详情响应: 成功={}/{}", 
            results.stream().filter(r -> r.getBoolean("success")).count(), 
            results.size());
        
        return JSON.toJSONString(results);
    }

    private String get(String url) {
        Request httpRequest = new Request.Builder()
                .url(url)
                .addHeader("appid", properties.getAppId())
                .addHeader("secret", properties.getSecret())
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        try (Response response = okHttpClient.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                log.error("得理法搜API请求失败: {}, {}", response.code(), response.message());
                throw new RuntimeException("得理法搜API请求失败: " + response.code());
            }

            String responseBody = response.body().string();
            log.debug("得理法搜API响应: {}", responseBody);
            return responseBody;

        } catch (IOException e) {
            log.error("得理法搜API请求异常", e);
            throw new RuntimeException("得理法搜API请求异常: " + e.getMessage());
        }
    }

    private String post(String url, JSONObject jsonBody) {
        RequestBody body = RequestBody.create(jsonBody.toJSONString(), MEDIA_TYPE_JSON);
        Request httpRequest = new Request.Builder()
                .url(url)
                .addHeader("appid", properties.getAppId())
                .addHeader("secret", properties.getSecret())
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = okHttpClient.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                log.error("得理法搜API请求失败: {}, {}", response.code(), response.message());
                throw new RuntimeException("得理法搜API请求失败: " + response.code());
            }

            String responseBody = response.body().string();
            log.info("得理法搜API响应: {}", responseBody);
            return responseBody;

        } catch (IOException e) {
            log.error("得理法搜API请求异常", e);
            throw new RuntimeException("得理法搜API请求异常: " + e.getMessage());
        }
    }
}