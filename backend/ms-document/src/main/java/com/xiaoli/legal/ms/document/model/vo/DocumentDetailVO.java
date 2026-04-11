package com.xiaoli.legal.ms.document.model.vo;

import java.time.LocalDateTime;

/**
 * 文书详情VO
 */
public class DocumentDetailVO {

    /**
     * 文书ID
     */
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
     * 模板名称
     */
    private String templateName;

    /**
     * 文书内容
     */
    private String content;

    /**
     * PDF文件URL
     */
    private String pdfUrl;

    /**
     * 文书状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

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

    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
