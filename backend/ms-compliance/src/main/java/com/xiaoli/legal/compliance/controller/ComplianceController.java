package com.xiaoli.legal.compliance.controller;

import com.xiaoli.legal.common.core.domain.Result;
import com.xiaoli.legal.compliance.model.dto.CompanyRequest;
import com.xiaoli.legal.compliance.model.dto.ComplianceResponse;
import com.xiaoli.legal.compliance.model.dto.ComplianceReviewRequest;
import com.xiaoli.legal.compliance.model.entity.Company;
import com.xiaoli.legal.compliance.model.entity.ComplianceReview;
import com.xiaoli.legal.compliance.model.entity.ComplianceRisk;
import com.xiaoli.legal.compliance.service.ComplianceService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 企业合规管理控制器
 */
@RestController
@RequestMapping("/api/compliance")
public class ComplianceController {
    
    private static final Logger log = LoggerFactory.getLogger(ComplianceController.class);
    
    private final ComplianceService complianceService;
    
    public ComplianceController(ComplianceService complianceService) {
        this.complianceService = complianceService;
    }
    
    /**
     * 创建企业
     */
    @PostMapping("/company")
    public Result<Company> createCompany(@Valid @RequestBody CompanyRequest request) {
        log.info("创建企业: {}", request.getName());
        Company company = complianceService.createCompany(request);
        return Result.success(company);
    }
    
    /**
     * 更新企业
     */
    @PutMapping("/company/{id}")
    public Result<Company> updateCompany(@PathVariable Long id, @Valid @RequestBody CompanyRequest request) {
        log.info("更新企业: {}", id);
        Company company = complianceService.updateCompany(id, request);
        return Result.success(company);
    }
    
    /**
     * 获取企业详情
     */
    @GetMapping("/company/{id}")
    public Result<Company> getCompany(@PathVariable Long id) {
        Company company = complianceService.getCompanyById(id);
        return Result.success(company);
    }
    
    /**
     * 获取企业列表
     */
    @GetMapping("/company/list")
    public Result<List<Company>> getCompanyList(
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Company> list = complianceService.getCompanyList(industry, riskLevel, pageNum, pageSize);
        return Result.success(list);
    }
    
    /**
     * 进行合规审查
     */
    @PostMapping("/review")
    public Result<ComplianceResponse> conductReview(@Valid @RequestBody ComplianceReviewRequest request) {
        log.info("开始合规审查, companyId: {}, type: {}", request.getCompanyId(), request.getReviewType());
        ComplianceResponse response = complianceService.conductReview(request);
        return Result.success(response);
    }
    
    /**
     * 获取审查结果
     */
    @GetMapping("/review/{id}")
    public Result<ComplianceReview> getReview(@PathVariable Long id) {
        ComplianceReview review = complianceService.getReviewById(id);
        return Result.success(review);
    }
    
    /**
     * 获取审查列表
     */
    @GetMapping("/review/list")
    public Result<List<ComplianceReview>> getReviewList(
            @RequestParam Long companyId,
            @RequestParam(required = false) String reviewType,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        List<ComplianceReview> list = complianceService.getReviewList(companyId, reviewType, pageNum, pageSize);
        return Result.success(list);
    }
    
    /**
     * 识别合规风险
     */
    @GetMapping("/risk/identify")
    public Result<List<ComplianceRisk>> identifyRisks(@RequestParam Long companyId) {
        log.info("识别企业风险, companyId: {}", companyId);
        List<ComplianceRisk> risks = complianceService.identifyRisks(companyId);
        return Result.success(risks);
    }
    
    /**
     * 获取风险列表
     */
    @GetMapping("/risk/list")
    public Result<List<ComplianceRisk>> getRiskList(
            @RequestParam Long companyId,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(required = false) String status) {
        List<ComplianceRisk> risks = complianceService.getRiskList(companyId, riskLevel, status);
        return Result.success(risks);
    }
    
    /**
     * 更新风险状态
     */
    @PutMapping("/risk/{id}/status")
    public Result<ComplianceRisk> updateRiskStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        log.info("更新风险状态, riskId: {}, status: {}", id, status);
        ComplianceRisk risk = complianceService.updateRiskStatus(id, status);
        return Result.success(risk);
    }
    
    /**
     * 计算合规评分
     */
    @GetMapping("/score/{companyId}")
    public Result<Integer> calculateScore(@PathVariable Long companyId) {
        Integer score = complianceService.calculateComplianceScore(companyId);
        return Result.success(score);
    }
    
    /**
     * 生成合规报告
     */
    @GetMapping("/report/{companyId}")
    public Result<String> generateReport(@PathVariable Long companyId) {
        log.info("生成合规报告, companyId: {}", companyId);
        String report = complianceService.generateComplianceReport(companyId);
        return Result.success(report);
    }
}
