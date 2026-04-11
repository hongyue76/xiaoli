package com.xiaoli.legal.ms.document.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 文书模板
 */
@TableName("document_template")
public class DocumentTemplate {

    /**
     * 模板ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模板类型: PLAINTIFF-起诉状, ANSWER-答辩状, APPEAL-上诉状, DEFENSE-辩护词, PROXY-代理词, CONTRACT-合同协议, OTHER-其他
     */
    private String templateType;

    /**
     * 案件类型: DIVORCE-婚姻家庭, CONTRACT-合同纠纷, LABOR-劳动争议, INHERITANCE-继承, TORT-侵权, CRIMINAL-刑事, ADMIN-行政, CIVIL-民事
     */
    private String caseType;

    /**
     * 模板描述
     */
    private String description;

    /**
     * 模板内容(FTL)
     */
    private String content;

    /**
     * 模板变量(JSON格式)
     */
    private String variables;

    /**
     * 模板状态: DRAFT-草稿, PUBLISHED-已发布, DISABLED-已禁用
     */
    private String status;

    /**
     * 使用次数
     */
    private Integer usageCount;

    /**
     * 创建人ID
     */
    private Long createBy;

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

    public String getTemplateType() { return templateType; }
    public void setTemplateType(String templateType) { this.templateType = templateType; }

    public String getCaseType() { return caseType; }
    public void setCaseType(String caseType) { this.caseType = caseType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getVariables() { return variables; }
    public void setVariables(String variables) { this.variables = variables; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getUsageCount() { return usageCount; }
    public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }

    public Long getCreateBy() { return createBy; }
    public void setCreateBy(Long createBy) { this.createBy = createBy; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
