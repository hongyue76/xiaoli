package com.xiaoli.legal.ms.document.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 法律文书
 */
@TableName("legal_document")
public class LegalDocument {

    /**
     * 文书ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文书标题
     */
    private String title;

    /**
     * 文书类型
     */
    private String docType;

    /**
     * 案件ID
     */
    private Long caseId;

    /**
     * 模板ID
     */
    private Long templateId;

    /**
     * 文书内容(Markdown格式)
     */
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private String content;

    /**
     * PDF文件路径
     */
    private String pdfPath;

    /**
     * 文书状态: DRAFT-草稿, REVIEW-审核中, APPROVED-已批准, SIGNED-已签署, FILED-已提交
     */
    private String status;

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

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDocType() { return docType; }
    public void setDocType(String docType) { this.docType = docType; }

    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }

    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getPdfPath() { return pdfPath; }
    public void setPdfPath(String pdfPath) { this.pdfPath = pdfPath; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
