package com.xiaoli.legal.ms.document.model.vo;

import java.time.LocalDateTime;

/**
 * 文档版本VO
 */
public class DocumentVersionVO {

    /**
     * 版本ID
     */
    private Long id;

    /**
     * 文档ID
     */
    private Long documentId;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 版本说明
     */
    private String description;

    /**
     * 修改类型
     */
    private String changeType;

    /**
     * 差异摘要
     */
    private String diffSummary;

    /**
     * 创建者ID
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 内容预览（前100字）
     */
    private String contentPreview;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDocumentId() { return documentId; }
    public void setDocumentId(Long documentId) { this.documentId = documentId; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getChangeType() { return changeType; }
    public void setChangeType(String changeType) { this.changeType = changeType; }

    public String getDiffSummary() { return diffSummary; }
    public void setDiffSummary(String diffSummary) { this.diffSummary = diffSummary; }

    public Long getCreateBy() { return createBy; }
    public void setCreateBy(Long createBy) { this.createBy = createBy; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public String getContentPreview() { return contentPreview; }
    public void setContentPreview(String contentPreview) { this.contentPreview = contentPreview; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long documentId;
        private Integer version;
        private String description;
        private String changeType;
        private String diffSummary;
        private Long createBy;
        private LocalDateTime createTime;
        private String contentPreview;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder documentId(Long documentId) { this.documentId = documentId; return this; }
        public Builder version(Integer version) { this.version = version; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder changeType(String changeType) { this.changeType = changeType; return this; }
        public Builder diffSummary(String diffSummary) { this.diffSummary = diffSummary; return this; }
        public Builder createBy(Long createBy) { this.createBy = createBy; return this; }
        public Builder createTime(LocalDateTime createTime) { this.createTime = createTime; return this; }
        public Builder contentPreview(String contentPreview) { this.contentPreview = contentPreview; return this; }

        public DocumentVersionVO build() {
            DocumentVersionVO vo = new DocumentVersionVO();
            vo.setId(id);
            vo.setDocumentId(documentId);
            vo.setVersion(version);
            vo.setDescription(description);
            vo.setChangeType(changeType);
            vo.setDiffSummary(diffSummary);
            vo.setCreateBy(createBy);
            vo.setCreateTime(createTime);
            vo.setContentPreview(contentPreview);
            return vo;
        }
    }
}
