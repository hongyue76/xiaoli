package com.xiaoli.legal.compliance.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 合规审查请求DTO
 */
public class ComplianceReviewRequest {
    
    /** 企业ID */
    @NotNull(message = "企业ID不能为空")
    private Long companyId;
    
    /** 审查类型 */
    @NotBlank(message = "审查类型不能为空")
    private String reviewType;
    
    /** 审查标题 */
    private String title;
    
    /** 审查范围 */
    private String scope;
    
    /** 审查要点 */
    private List<String> reviewPoints;
    
    /** 内部制度文档列表 */
    private List<Map<String, String>> documents;
    
    /** 业务流程描述 */
    private List<Map<String, String>> businessProcesses;
    
    /** 合同清单 */
    private List<Map<String, String>> contracts;
    
    /** 员工信息 */
    private Map<String, Object> employeeInfo;
    
    /** 财务信息 */
    private Map<String, Object> financialInfo;
    
    /** 数据安全措施 */
    private Map<String, Object> dataSecurityMeasures;

    // Getters and Setters
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getReviewType() { return reviewType; }
    public void setReviewType(String reviewType) { this.reviewType = reviewType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    public List<String> getReviewPoints() { return reviewPoints; }
    public void setReviewPoints(List<String> reviewPoints) { this.reviewPoints = reviewPoints; }

    public List<Map<String, String>> getDocuments() { return documents; }
    public void setDocuments(List<Map<String, String>> documents) { this.documents = documents; }

    public List<Map<String, String>> getBusinessProcesses() { return businessProcesses; }
    public void setBusinessProcesses(List<Map<String, String>> businessProcesses) { this.businessProcesses = businessProcesses; }

    public List<Map<String, String>> getContracts() { return contracts; }
    public void setContracts(List<Map<String, String>> contracts) { this.contracts = contracts; }

    public Map<String, Object> getEmployeeInfo() { return employeeInfo; }
    public void setEmployeeInfo(Map<String, Object> employeeInfo) { this.employeeInfo = employeeInfo; }

    public Map<String, Object> getFinancialInfo() { return financialInfo; }
    public void setFinancialInfo(Map<String, Object> financialInfo) { this.financialInfo = financialInfo; }

    public Map<String, Object> getDataSecurityMeasures() { return dataSecurityMeasures; }
    public void setDataSecurityMeasures(Map<String, Object> dataSecurityMeasures) { this.dataSecurityMeasures = dataSecurityMeasures; }
}
