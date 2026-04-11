package com.xiaoli.legal.ms.contract.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 合同
 */
@TableName("contract")
public class Contract {

    /**
     * 合同ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 合同名称
     */
    private String name;

    /**
     * 合同类型
     */
    private String contractType;

    /**
     * 合同甲方
     */
    private String partyA;

    /**
     * 合同乙方
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
    private LocalDateTime signDate;

    /**
     * 合同文件路径
     */
    private String filePath;

    /**
     * 合同内容(OCR识别)
     */
    private String content;

    /**
     * 审查状态: PENDING-待审查, REVIEWING-审查中, COMPLETED-已完成
     */
    private String reviewStatus;

    /**
     * 风险等级: LOW-低, MEDIUM-中, HIGH-高
     */
    private String riskLevel;

    /**
     * 审查结果JSON
     */
    private String reviewResult;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public LocalDateTime getSignDate() { return signDate; }
    public void setSignDate(LocalDateTime signDate) { this.signDate = signDate; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getReviewStatus() { return reviewStatus; }
    public void setReviewStatus(String reviewStatus) { this.reviewStatus = reviewStatus; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public String getReviewResult() { return reviewResult; }
    public void setReviewResult(String reviewResult) { this.reviewResult = reviewResult; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
