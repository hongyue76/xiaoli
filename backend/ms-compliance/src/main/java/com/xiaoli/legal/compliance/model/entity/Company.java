package com.xiaoli.legal.compliance.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 企业实体
 */
@Entity
@Table(name = "company")
public class Company {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 企业名称 */
    @Column(name = "name", length = 200)
    private String name;
    
    /** 统一社会信用代码 */
    @Column(name = "credit_code", length = 50)
    private String creditCode;
    
    /** 企业类型 */
    @Column(name = "company_type", length = 50)
    private String companyType;
    
    /** 所属行业 */
    @Column(name = "industry", length = 50)
    private String industry;
    
    /** 注册资本 */
    @Column(name = "registered_capital")
    private Long registeredCapital;
    
    /** 经营范围 */
    @Column(name = "business_scope", columnDefinition = "TEXT")
    private String businessScope;
    
    /** 注册地址 */
    @Column(name = "registered_address", length = 500)
    private String registeredAddress;
    
    /** 联系人 */
    @Column(name = "contact_person", length = 50)
    private String contactPerson;
    
    /** 联系电话 */
    @Column(name = "contact_phone", length = 20)
    private String contactPhone;
    
    /** 邮箱 */
    @Column(name = "email", length = 100)
    private String email;
    
    /** 风险等级: HIGH/MEDIUM/LOW */
    @Column(name = "risk_level", length = 20)
    private String riskLevel;
    
    /** 综合合规评分 */
    @Column(name = "compliance_score")
    private Integer complianceScore;
    
    /** 最后合规检查时间 */
    @Column(name = "last_check_time")
    private LocalDateTime lastCheckTime;
    
    /** 下次合规检查时间 */
    @Column(name = "next_check_time")
    private LocalDateTime nextCheckTime;
    
    /** 创建时间 */
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
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
    
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    
    public Integer getComplianceScore() { return complianceScore; }
    public void setComplianceScore(Integer complianceScore) { this.complianceScore = complianceScore; }
    
    public LocalDateTime getLastCheckTime() { return lastCheckTime; }
    public void setLastCheckTime(LocalDateTime lastCheckTime) { this.lastCheckTime = lastCheckTime; }
    
    public LocalDateTime getNextCheckTime() { return nextCheckTime; }
    public void setNextCheckTime(LocalDateTime nextCheckTime) { this.nextCheckTime = nextCheckTime; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
