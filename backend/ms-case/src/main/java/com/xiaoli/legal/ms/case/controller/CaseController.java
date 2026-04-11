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

        if (keywords == null || keywords.isEmpty()) {
            return Result.fail("keywords参数不能为空");
        }

        String result = delilegalService.searchLaws(String.join(",", keywords), fieldName);
        return Result.success(result);
    }
}
