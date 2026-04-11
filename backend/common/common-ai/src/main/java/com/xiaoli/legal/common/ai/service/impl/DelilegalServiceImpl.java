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
import java.util.ArrayList;
import java.util.List;

/**
 * 得理法搜案例检索服务实现
 */
@Service
public class DelilegalServiceImpl implements DelilegalService {

    private static final Logger log = LoggerFactory.getLogger(DelilegalServiceImpl.class);

    private final OkHttpClient okHttpClient;
    private final DelilegalProperties properties;

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

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