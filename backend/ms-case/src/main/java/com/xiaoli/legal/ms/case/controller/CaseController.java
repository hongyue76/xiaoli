package com.xiaoli.legal.ms.legalcase.controller;

import com.xiaoli.legal.common.ai.service.DelilegalService;
import com.xiaoli.legal.common.core.domain.PageResult;
import com.xiaoli.legal.common.core.domain.Result;
import com.xiaoli.legal.ms.legalcase.model.dto.CaseSearchRequest;
import com.xiaoli.legal.ms.legalcase.model.vo.CaseSearchResultVO;
import com.xiaoli.legal.ms.legalcase.model.vo.JudgeProfileVO;
import com.xiaoli.legal.ms.legalcase.service.CaseSearchService;
import com.xiaoli.legal.ms.legalcase.service.JudgeProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 案例检索接口
 */
@RestController
@RequestMapping("/api/case")
public class CaseController {

    private static final Logger log = LoggerFactory.getLogger(CaseController.class);

    private final CaseSearchService caseSearchService;
    private final JudgeProfileService judgeProfileService;
    private final DelilegalService delilegalService;

    public CaseController(CaseSearchService caseSearchService, 
                          JudgeProfileService judgeProfileService,
                          DelilegalService delilegalService) {
        this.caseSearchService = caseSearchService;
        this.judgeProfileService = judgeProfileService;
        this.delilegalService = delilegalService;
    }

    /**
     * 案例检索
     */
    @PostMapping("/search")
    public Result<PageResult<CaseSearchResultVO>> searchCases(
            @RequestBody @Validated CaseSearchRequest request) {
        log.info("案例检索请求: keyword={}, caseType={}", request.getKeyword(), request.getCaseType());
        PageResult<CaseSearchResultVO> result = caseSearchService.searchCases(request);
        return Result.success(result);
    }

    /**
     * 语义检索（用于相似案例推荐）
     */
    @PostMapping("/semantic/search")
    public Result<PageResult<CaseSearchResultVO>> semanticSearch(
            @RequestBody @Validated CaseSearchRequest request) {
        log.info("语义检索请求: keyword={}", request.getKeyword());
        // 设置默认分页参数
        if (request.getCurrent() == null || request.getCurrent() < 1) {
            request.setCurrent(1L);
        }
        if (request.getSize() == null || request.getSize() < 1) {
            request.setSize(5L);
        }
        PageResult<CaseSearchResultVO> result = caseSearchService.semanticSearch(request);
        return Result.success(result);
    }

    /**
     * 获取案例详情
     */
    @GetMapping("/{id}")
    public Result<CaseSearchResultVO> getCaseDetail(@PathVariable Long id) {
        CaseSearchResultVO result = caseSearchService.getCaseDetail(id);
        return Result.success(result);
    }

    /**
     * 获取相似案例
     */
    @GetMapping("/{id}/similar")
    public Result<List<CaseSearchResultVO>> getSimilarCases(
            @PathVariable Long id,
            @RequestParam(defaultValue = "5") Long limit) {
        List<CaseSearchResultVO> result = caseSearchService.getSimilarCases(id, limit);
        return Result.success(result);
    }

    /**
     * 获取热门案例
     */
    @GetMapping("/hot")
    public Result<List<CaseSearchResultVO>> getHotCases(
            @RequestParam(required = false) String caseType,
            @RequestParam(defaultValue = "10") Long limit) {
        List<CaseSearchResultVO> result = caseSearchService.getHotCases(caseType, limit);
        return Result.success(result);
    }

    /**
     * 获取案件类型列表
     */
    @GetMapping("/types")
    public Result<List<String>> getCaseTypes() {
        List<String> types = List.of(
                "CIVIL",           // 民事
                "CRIMINAL",       // 刑事
                "ADMINISTRATIVE", // 行政
                "ARBITRATION"     // 仲裁
        );
        return Result.success(types);
    }

    /**
     * 获取案件类型细分
     */
    @GetMapping("/types/{type}")
    public Result<List<String>> getCaseSubTypes(@PathVariable String type) {
        List<String> subTypes;
        switch (type) {
            case "CIVIL":
                subTypes = List.of(
                        "CONTRACT_DISPUTE",    // 合同纠纷
                        "PROPERTY_DISPUTE",   // 财产纠纷
                        "MARRIAGE",           // 婚姻家庭
                        "INHERITANCE",        // 继承纠纷
                        "TORT",               // 侵权纠纷
                        "LABOR_DISPUTE",      // 劳动争议
                        "COMPANY"             // 公司法务
                );
                break;
            case "CRIMINAL":
                subTypes = List.of(
                        "THEFT",              // 盗窃
                        "FRAUD",              // 诈骗
                        "ASSAULT",            // 伤害
                        "DRUG",               // 毒品犯罪
                        "ECONOMIC_CRIME"      // 经济犯罪
                );
                break;
            default:
                subTypes = List.of();
        }
        return Result.success(subTypes);
    }

    /**
     * 按法官搜索案例
     */
    @GetMapping("/judge/search")
    public Result<PageResult<CaseSearchResultVO>> searchByJudge(
            @RequestParam String judgeName,
            @RequestParam(required = false) String caseType,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        log.info("按法官搜索案例: judgeName={}, caseType={}", judgeName, caseType);
        PageResult<CaseSearchResultVO> result = caseSearchService.searchByJudge(judgeName, caseType, current, size);
        return Result.success(result);
    }

    /**
     * 法官画像分析
     */
    @GetMapping("/judge/profile")
    public Result<JudgeProfileVO> analyzeJudgeProfile(
            @RequestParam String judgeName,
            @RequestParam(required = false) String caseType) {
        log.info("法官画像分析: judgeName={}, caseType={}", judgeName, caseType);
        JudgeProfileVO profile = judgeProfileService.analyzeJudgeProfile(judgeName, caseType);
        return Result.success(profile);
    }

    /**
     * 得理法搜案例检索
     * 接口地址：https://openapi.delilegal.com/api/qa/v3/search/queryListCase
     */
    @PostMapping("/delilegal/search")
    public Result<String> searchCasesByDelilegal(@RequestBody java.util.Map<String, Object> request) {
        log.info("得理法搜案例检索: request={}", request);

        List<String> keywordList = null;
        if (request.containsKey("keywords") && request.get("keywords") != null) {
            Object keywords = request.get("keywords");
            if (keywords instanceof String) {
                keywordList = Arrays.asList(((String) keywords).split(","));
            } else if (keywords instanceof List) {
                keywordList = (List<String>) keywords;
            }
        }

        String longText = (String) request.get("longText");

        List<String> courtLevels = null;
        if (request.containsKey("courtLevelArr") && request.get("courtLevelArr") != null) {
            Object courtLevelsObj = request.get("courtLevelArr");
            if (courtLevelsObj instanceof String) {
                courtLevels = Arrays.asList(((String) courtLevelsObj).split(","));
            } else if (courtLevelsObj instanceof List) {
                courtLevels = (List<String>) courtLevelsObj;
            }
        }

        List<String> judgementTypes = null;
        if (request.containsKey("judgementTypeArr") && request.get("judgementTypeArr") != null) {
            Object typesObj = request.get("judgementTypeArr");
            if (typesObj instanceof String) {
                judgementTypes = Arrays.asList(((String) typesObj).split(","));
            } else if (typesObj instanceof List) {
                judgementTypes = (List<String>) typesObj;
            }
        }

        String result = delilegalService.searchCases(keywordList, longText, courtLevels, judgementTypes);
        return Result.success(result);
    }

    /**
     * 得理法搜法规检索
     * 接口地址：https://openapi.delilegal.com/api/qa/v3/search/queryListLaw
     * 
     * @param request 包含 keywords, fieldName, autoFetchDetail(可选，默认true)
     * @return 法规列表JSON，如果 autoFetchDetail=true 则自动获取完整内容
     */
    @PostMapping("/delilegal/law")
    public Result<String> searchLawsByDelilegal(@RequestBody java.util.Map<String, Object> request) {
        log.info("得理法搜法规检索: request={}", request);

        List<String> keywords = null;
        if (request.containsKey("keywords") && request.get("keywords") != null) {
            Object keywordsObj = request.get("keywords");
            if (keywordsObj instanceof String) {
                keywords = Arrays.asList(((String) keywordsObj).split(","));
            } else if (keywordsObj instanceof List) {
                keywords = (List<String>) keywordsObj;
            }
        }

        String fieldName = request.getOrDefault("fieldName", "title").toString();
        
        // 是否自动获取完整内容（默认开启）
        boolean autoFetchDetail = true;
        if (request.containsKey("autoFetchDetail")) {
            autoFetchDetail = Boolean.TRUE.equals(request.get("autoFetchDetail"));
        }

        if (keywords == null || keywords.isEmpty()) {
            return Result.fail("keywords参数不能为空");
        }

        // 先获取法规列表
        String result = delilegalService.searchLaws(String.join(",", keywords), fieldName);
        
        // 如果开启自动获取详情
        if (autoFetchDetail) {
            try {
                result = enrichLawsWithDetails(result);
            } catch (Exception e) {
                log.error("自动获取法规详情失败，继续返回原始结果", e);
                // 不中断流程，返回原始结果
            }
        }
        
        return Result.success(result);
    }

    /**
     * 获取法规详情
     * 接口地址：https://openapi.delilegal.com/api/qa/v3/search/lawInfo
     * 
     * @param lawId 法规ID（必填）
     * @param merge 是否合并内容（默认true，返回完整正文）
     * @return 法规详情JSON，包含 lawDetailContent 字段
     */
    @GetMapping("/delilegal/law/detail")
    public Result<String> getLawDetail(
            @RequestParam String lawId,
            @RequestParam(defaultValue = "true") boolean merge) {
        log.info("得理法搜法规详情请求: lawId={}, merge={}", lawId, merge);
        
        if (lawId == null || lawId.isEmpty()) {
            return Result.fail("lawId参数不能为空");
        }
        
        String result = delilegalService.getLawDetail(lawId, merge);
        return Result.success(result);
    }

    /**
     * 批量获取法规详情（并发优化）
     * 
     * @param lawIds 法规ID列表（逗号分隔或JSON数组）
     * @param merge 是否合并内容（默认true）
     * @return 法规详情列表JSON，失败项标记 fullContent: null
     */
    @PostMapping("/delilegal/law/batch")
    public Result<String> getBatchLawDetails(
            @RequestParam String lawIds,
            @RequestParam(defaultValue = "true") boolean merge) {
        log.info("得理法搜批量法规详情请求: lawIds={}, merge={}", lawIds, merge);
        
        List<String> lawIdList;
        if (lawIds.startsWith("[")) {
            // JSON数组格式
            lawIdList = com.alibaba.fastjson2.JSON.parseArray(lawIds, String.class);
        } else {
            // 逗号分隔格式
            lawIdList = Arrays.asList(lawIds.split(","));
        }
        
        if (lawIdList == null || lawIdList.isEmpty()) {
            return Result.fail("lawIds参数不能为空");
        }
        
        String result = delilegalService.getBatchLawDetails(lawIdList, merge);
        return Result.success(result);
    }

    /**
     * 增强法规检索结果，自动获取每个法规的完整内容
     * 
     * @param lawsJson 法规检索返回的JSON
     * @return 增强后的JSON，包含 fullContent 字段
     */
    private String enrichLawsWithDetails(String lawsJson) {
        com.alibaba.fastjson2.JSONObject response = 
            com.alibaba.fastjson2.JSON.parseObject(lawsJson);
        
        if (response == null) {
            return lawsJson;
        }
        
        // 提取法规列表（根据实际返回结构调整）
        com.alibaba.fastjson2.JSONArray lawList = null;
        
        // 尝试不同的可能字段名
        if (response.containsKey("data")) {
            Object data = response.get("data");
            if (data instanceof com.alibaba.fastjson2.JSONArray) {
                lawList = (com.alibaba.fastjson2.JSONArray) data;
            } else if (data instanceof com.alibaba.fastjson2.JSONObject) {
                lawList = ((com.alibaba.fastjson2.JSONObject) data).getJSONArray("list");
            }
        } else if (response.containsKey("result")) {
            Object result = response.get("result");
            if (result instanceof com.alibaba.fastjson2.JSONArray) {
                lawList = (com.alibaba.fastjson2.JSONArray) result;
            }
        } else if (response.containsKey("laws")) {
            lawList = response.getJSONArray("laws");
        }
        
        if (lawList == null || lawList.isEmpty()) {
            log.info("未找到法规列表，跳过详情获取");
            return lawsJson;
        }
        
        log.info("开始批量获取 {} 个法规的详情", lawList.size());
        
        // 收集所有 lawId
        List<String> lawIds = new java.util.ArrayList<>();
        for (int i = 0; i < lawList.size(); i++) {
            com.alibaba.fastjson2.JSONObject law = lawList.getJSONObject(i);
            String lawId = law.getString("id");
            if (lawId != null) {
                lawIds.add(lawId);
            }
        }
        
        // 批量获取详情
        String batchResult = delilegalService.getBatchLawDetails(lawIds, true);
        List<com.alibaba.fastjson2.JSONObject> details = 
            com.alibaba.fastjson2.JSON.parseArray(batchResult, com.alibaba.fastjson2.JSONObject.class);
        
        // 建立 lawId -> 详情的映射
        Map<String, com.alibaba.fastjson2.JSONObject> detailMap = new java.util.HashMap<>();
        if (details != null) {
            for (com.alibaba.fastjson2.JSONObject detail : details) {
                detailMap.put(detail.getString("lawId"), detail);
            }
        }
        
        // 将详情合并到原法规对象
        for (int i = 0; i < lawList.size(); i++) {
            com.alibaba.fastjson2.JSONObject law = lawList.getJSONObject(i);
            String lawId = law.getString("id");
            
            if (lawId != null && detailMap.containsKey(lawId)) {
                com.alibaba.fastjson2.JSONObject detail = detailMap.get(lawId);
                law.put("fullContent", detail.get("fullContent"));
                law.put("fullContentPreview", detail.get("fullContentPreview"));
                law.put("detailSuccess", detail.get("success"));
                
                if (detail.getBoolean("success") == null || !detail.getBoolean("success")) {
                    law.put("detailError", detail.get("error"));
                }
            } else {
                law.put("fullContent", null);
                law.put("fullContentPreview", null);
                law.put("detailSuccess", false);
                law.put("detailError", "未找到对应详情");
            }
        }
        
        log.info("法规详情获取完成，成功: {}/{}", 
            java.util.stream.IntStream.range(0, lawList.size())
                .filter(i -> {
                    com.alibaba.fastjson2.JSONObject obj = lawList.getJSONObject(i);
                    return obj != null && Boolean.TRUE.equals(obj.get("detailSuccess"));
                }).count(),
            lawList.size());
        
        return com.alibaba.fastjson2.JSON.toJSONString(response);
    }
}
