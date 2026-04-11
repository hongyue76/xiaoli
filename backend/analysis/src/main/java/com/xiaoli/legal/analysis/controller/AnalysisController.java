package com.xiaoli.legal.analysis.controller;

import com.xiaoli.legal.analysis.model.dto.CaseAnalysisRequest;
import com.xiaoli.legal.analysis.model.dto.CaseAnalysisResponse;
import com.xiaoli.legal.analysis.model.entity.CaseAnalysis;
import com.xiaoli.legal.common.core.domain.Result;
import com.xiaoli.legal.analysis.service.CaseAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 案件分析接口
 */
@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private static final Logger log = LoggerFactory.getLogger(AnalysisController.class);

    private final CaseAnalysisService analysisService;

    public AnalysisController(CaseAnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    /**
     * 分析案件
     */
    @PostMapping("/case")
    public Result<CaseAnalysisResponse> analyzeCase(
            @RequestBody @Validated CaseAnalysisRequest request,
            @RequestParam(defaultValue = "1") Long userId) {
        log.info("收到案件分析请求: {}", request.getCaseTitle());
        CaseAnalysisResponse result = analysisService.analyzeCase(request, userId);
        return Result.success(result);
    }

    /**
     * 获取分析结果
     */
    @GetMapping("/{id}")
    public Result<CaseAnalysisResponse> getAnalysisResult(@PathVariable Long id) {
        CaseAnalysisResponse result = analysisService.getAnalysisResult(id);
        return Result.success(result);
    }

    /**
     * 获取分析历史
     */
    @GetMapping("/history")
    public Result<List<CaseAnalysis>> getAnalysisHistory(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long caseId) {
        List<CaseAnalysis> result = analysisService.getAnalysisHistory(userId, caseId);
        return Result.success(result);
    }

    /**
     * 删除分析记录
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteAnalysis(@PathVariable Long id) {
        analysisService.deleteAnalysis(id);
        return Result.success();
    }

    /**
     * 获取案件类型
     */
    @GetMapping("/case-types")
    public Result<List<String>> getCaseTypes() {
        List<String> types = List.of(
                "CONTRACT_DISPUTE",   // 合同纠纷
                "MARRIAGE",           // 婚姻家庭
                "LABOR_DISPUTE",      // 劳动争议
                "TORT",               // 侵权纠纷
                "PROPERTY_DISPUTE",   // 财产纠纷
                "INHERITANCE",        // 继承纠纷
                "COMPANY_DISPUTE",    // 公司纠纷
                "REAL_ESTATE",        // 房地产纠纷
                "OTHER"               // 其他
        );
        return Result.success(types);
    }
}
