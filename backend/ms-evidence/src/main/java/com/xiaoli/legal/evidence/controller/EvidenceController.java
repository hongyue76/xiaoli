package com.xiaoli.legal.evidence.controller;

import com.xiaoli.legal.common.core.domain.Result;
import com.xiaoli.legal.evidence.model.dto.EvidenceAnalysisRequest;
import com.xiaoli.legal.evidence.model.dto.EvidenceAnalysisResponse;
import com.xiaoli.legal.evidence.model.entity.Evidence;
import com.xiaoli.legal.evidence.service.EvidenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 证据材料分析接口
 */
@RestController
@RequestMapping("/api/evidence")
public class EvidenceController {

    private static final Logger log = LoggerFactory.getLogger(EvidenceController.class);

    private final EvidenceService evidenceService;

    public EvidenceController(EvidenceService evidenceService) {
        this.evidenceService = evidenceService;
    }

    /**
     * 分析证据材料
     */
    @PostMapping("/analyze")
    public Result<EvidenceAnalysisResponse> analyzeEvidence(
            @RequestBody @Validated EvidenceAnalysisRequest request,
            @RequestParam(defaultValue = "1") Long userId) {
        log.info("收到证据分析请求，证据数量: {}", request.getEvidences().size());
        EvidenceAnalysisResponse result = evidenceService.analyzeEvidence(request, userId);
        return Result.success(result);
    }

    /**
     * 获取证据详情
     */
    @GetMapping("/{id}")
    public Result<Evidence> getEvidenceDetail(@PathVariable Long id) {
        Evidence result = evidenceService.getEvidenceDetail(id);
        return Result.success(result);
    }

    /**
     * 获取分析结果
     */
    @GetMapping("/{id}/analysis")
    public Result<EvidenceAnalysisResponse> getAnalysisResult(@PathVariable Long id) {
        EvidenceAnalysisResponse result = evidenceService.getAnalysisResult(id);
        return Result.success(result);
    }

    /**
     * 获取证据列表
     */
    @GetMapping("/list")
    public Result<List<Evidence>> getEvidenceList(
            @RequestParam(required = false) Long caseId,
            @RequestParam(required = false) Long userId) {
        List<Evidence> result = evidenceService.getEvidenceList(caseId, userId);
        return Result.success(result);
    }

    /**
     * 删除证据
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteEvidence(@PathVariable Long id) {
        evidenceService.deleteEvidence(id);
        return Result.success();
    }

    /**
     * 获取证据类型
     */
    @GetMapping("/types")
    public Result<List<String>> getEvidenceTypes() {
        List<String> types = evidenceService.getEvidenceTypes();
        return Result.success(types);
    }
}
