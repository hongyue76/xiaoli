package com.xiaoli.legal.evidence.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 证据材料
 */
@TableName("evidence")
public class Evidence {

    /**
     * 证据ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 证据名称
     */
    private String name;

    /**
     * 证据类型: DOCUMENT-书证, OBJECT-物证, AUDIO-视听资料, ELECTRONIC-电子数据, EXPERT-鉴定意见, WITNESS-证人证言, STATEMENT-当事人陈述, INSPECTION-勘验笔录
     */
    private String evidenceType;

    /**
     * 证据来源: PLAINTIFF-原告提供, DEFENDANT-被告提供, COURT-法院调取, THIRD_PARTY-第三人提供
     */
    private String source;

    /**
     * 证明目的
     */
    private String purpose;

    /**
     * 证据内容摘要
     */
    private String content;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 案件ID
     */
    private Long caseId;

    /**
     * 审查状态: PENDING-待审查, REVIEWING-审查中, COMPLETED-已完成
     */
    private String reviewStatus;

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

    public String getEvidenceType() { return evidenceType; }
    public void setEvidenceType(String evidenceType) { this.evidenceType = evidenceType; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }

    public String getReviewStatus() { return reviewStatus; }
    public void setReviewStatus(String reviewStatus) { this.reviewStatus = reviewStatus; }

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
