package com.xiaoli.legal.compliance.service;

import com.xiaoli.legal.compliance.model.dto.CompanyRequest;
import com.xiaoli.legal.compliance.model.dto.ComplianceResponse;
import com.xiaoli.legal.compliance.model.dto.ComplianceReviewRequest;
import com.xiaoli.legal.compliance.model.entity.Company;
import com.xiaoli.legal.compliance.model.entity.ComplianceReview;
import com.xiaoli.legal.compliance.model.entity.ComplianceRisk;

import java.util.List;

/**
 * 企业合规服务接口
 */
public interface ComplianceService {
    
    /**
     * 创建企业
     */
    Company createCompany(CompanyRequest request);
    
    /**
     * 更新企业信息
     */
    Company updateCompany(Long id, CompanyRequest request);
    
    /**
     * 获取企业详情
     */
    Company getCompanyById(Long id);
    
    /**
     * 获取企业列表
     */
    List<Company> getCompanyList(String industry, String riskLevel, Integer pageNum, Integer pageSize);
    
    /**
     * 进行合规审查
     */
    ComplianceResponse conductReview(ComplianceReviewRequest request);
    
    /**
     * 获取审查结果
     */
    ComplianceReview getReviewById(Long id);
    
    /**
     * 获取企业审查列表
     */
    List<ComplianceReview> getReviewList(Long companyId, String reviewType, Integer pageNum, Integer pageSize);
    
    /**
     * 识别合规风险
     */
    List<ComplianceRisk> identifyRisks(Long companyId);
    
    /**
     * 更新风险整改状态
     */
    ComplianceRisk updateRiskStatus(Long riskId, String status);
    
    /**
     * 获取风险列表
     */
    List<ComplianceRisk> getRiskList(Long companyId, String riskLevel, String status);
    
    /**
     * 计算企业合规评分
     */
    Integer calculateComplianceScore(Long companyId);
    
    /**
     * 生成合规报告
     */
    String generateComplianceReport(Long companyId);
}
