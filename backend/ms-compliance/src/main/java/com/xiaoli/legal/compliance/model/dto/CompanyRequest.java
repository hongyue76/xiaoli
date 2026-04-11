package com.xiaoli.legal.compliance.model.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 企业请求DTO
 */
public class CompanyRequest {
    
    /** 企业名称 */
    @NotBlank(message = "企业名称不能为空")
    private String name;
    
    /** 统一社会信用代码 */
    private String creditCode;
    
    /** 企业类型 */
    private String companyType;
    
    /** 所属行业 */
    private String industry;
    
    /** 注册资本 */
    private Long registeredCapital;
    
    /** 经营范围 */
    private String businessScope;
    
    /** 注册地址 */
    private String registeredAddress;
    
    /** 联系人 */
    private String contactPerson;
    
    /** 联系电话 */
    private String contactPhone;
    
    /** 邮箱 */
    private String email;
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getCreditCode() { return creditCode; }
    public void setCreditCode(String creditCode) { this.creditCode = creditCode; }
    
    public String getCompanyType() { return companyType; }
    public void setCompanyType(String companyType) { this.companyType = companyType; }
    
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    
    public Long getRegisteredCapital() { return registeredCapital; }
    public void setRegisteredCapital(Long registeredCapital) { this.registeredCapital = registeredCapital; }
    
    public String getBusinessScope() { return businessScope; }
    public void setBusinessScope(String businessScope) { this.businessScope = businessScope; }
    
    public String getRegisteredAddress() { return registeredAddress; }
    public void setRegisteredAddress(String registeredAddress) { this.registeredAddress = registeredAddress; }
    
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
