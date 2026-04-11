package com.xiaoli.legal.decision.controller;

import com.xiaoli.legal.common.core.domain.Result;
import com.xiaoli.legal.decision.model.dto.DecisionRequest;
import com.xiaoli.legal.decision.model.dto.DecisionResponse;
import com.xiaoli.legal.decision.service.DecisionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * 司法辅助决策控制器
 */
@RestController
@RequestMapping("/api/decision")
public class DecisionController {
    
    private static final Logger log = LoggerFactory.getLogger(DecisionController.class);
    
    private final DecisionService decisionService;
    
    public DecisionController(DecisionService decisionService) {
        this.decisionService = decisionService;
    }
    
    /**
     * 生成司法决策建议
     */
    @PostMapping("/generate")
    public Result<DecisionResponse> generateDecision(@Valid @RequestBody DecisionRequest request) {
        log.info("收到司法决策请求, caseType: {}", request.getCaseType());
        DecisionResponse response = decisionService.generateDecision(request);
        return Result.success(response);
    }
    
    /**
     * 获取量刑建议
     */
    @PostMapping("/sentencing")
    public Result<DecisionResponse.SentencingInfo> getSentencingSuggestion(@Valid @RequestBody DecisionRequest request) {
        log.info("收到量刑建议请求");
        DecisionResponse.SentencingInfo info = decisionService.getSentencingSuggestion(request);
        return Result.success(info);
    }
    
    /**
     * 获取审判预测
     */
    @PostMapping("/trial-prediction")
    public Result<DecisionResponse.TrialPredictionInfo> getTrialPrediction(@Valid @RequestBody DecisionRequest request) {
        log.info("收到审判预测请求");
        DecisionResponse.TrialPredictionInfo info = decisionService.getTrialPrediction(request);
        return Result.success(info);
    }
    
    /**
     * 查找相似判例
     */
    @GetMapping("/similar-cases")
    public Result<DecisionResponse.ReferenceCaseInfo> findSimilarJudgments(
            @RequestParam String caseType,
            @RequestParam String caseDescription) {
        log.info("查找相似判例, caseType: {}", caseType);
        DecisionResponse.ReferenceCaseInfo info = decisionService.findSimilarJudgments(caseType, caseDescription);
        return Result.success(info);
    }
    
    /**
     * 评估案件风险
     */
    @PostMapping("/risk-assessment")
    public Result<DecisionResponse.RiskAssessment> assessRisk(@Valid @RequestBody DecisionRequest request) {
        log.info("收到风险评估请求");
        DecisionResponse.RiskAssessment assessment = decisionService.assessRisk(request);
        return Result.success(assessment);
    }
    
    /**
     * 获取决策列表
     */
    @GetMapping("/list")
    public Result<?> getDecisionList(
            @RequestParam(required = false) String caseType,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        // TODO: 实现分页查询
        return Result.success(null);
    }
    
    /**
     * 获取决策详情
     */
    @GetMapping("/{id}")
    public Result<?> getDecisionDetail(@PathVariable Long id) {
        // TODO: 实现详情查询
        return Result.success(null);
    }
}
