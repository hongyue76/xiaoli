package com.xiaoli.legal.ms.contract.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 合同审查请求
 */
public class ContractReviewRequest {

    /**
     * 合同名称
     */
    @NotBlank(message = "合同名称不能为空")
    private String name;

    /**
     * 合同类型
     */
    @NotBlank(message = "合同类型不能为空")
    private String contractType;

    /**
     * 甲方
     */
    private String partyA;

    /**
     * 乙方
     */
    private String partyB;

    /**
     * 合同金额
     */
    private Long amount;

    /**
     * 合同期限
     */
    private String duration;

    /**
     * 签订日期
     */
    private String signDate;

    /**
     * 合同内容(文本)
     */
    private String content;

    /**
     * 审查维度
     */
    private List<String> reviewDimensions;

    /**
     * 是否启用AI增强
     */
    private Boolean aiEnhance = true;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContractType() { return contractType; }
    public void setContractType(String contractType) { this.contractType = contractType; }

    public String getPartyA() { return partyA; }
    public void setPartyA(String partyA) { this.partyA = partyA; }

    public String getPartyB() { return partyB; }
    public void setPartyB(String partyB) { this.partyB = partyB; }

    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getSignDate() { return signDate; }
    public void setSignDate(String signDate) { this.signDate = signDate; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<String> getReviewDimensions() { return reviewDimensions; }
    public void setReviewDimensions(List<String> reviewDimensions) { this.reviewDimensions = reviewDimensions; }

    public Boolean getAiEnhance() { return aiEnhance; }
    public void setAiEnhance(Boolean aiEnhance) { this.aiEnhance = aiEnhance; }
}
