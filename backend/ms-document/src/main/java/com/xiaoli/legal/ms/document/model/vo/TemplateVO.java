package com.xiaoli.legal.ms.document.model.vo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 模板VO
 */
public class TemplateVO {

    /**
     * 模板ID
     */
    private Long id;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模板类型
     */
    private String templateType;

    /**
     * 案件类型
     */
    private String caseType;

    /**
     * 模板描述
     */
    private String description;

    /**
     * 模板变量列表
     */
    private List<Variable> variables;

    /**
     * 使用次数
     */
    private Integer usageCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

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

    public List<Variable> getVariables() { return variables; }
    public void setVariables(List<Variable> variables) { this.variables = variables; }

    public Integer getUsageCount() { return usageCount; }
    public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    /**
     * 模板变量
     */
    public static class Variable {
        /**
         * 变量名
         */
        private String name;

        /**
         * 变量标签
         */
        private String label;

        /**
         * 变量类型: TEXT-文本, NUMBER-数字, DATE-日期, SELECT-选择, TEXTAREA-长文本
         */
        private String type;

        /**
         * 是否必填
         */
        private Boolean required;

        /**
         * 占位符
         */
        private String placeholder;

        /**
         * 选项列表(type为SELECT时使用)
         */
        private List<String> options;

        /**
         * 示例值
         */
        private String example;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public Boolean getRequired() { return required; }
        public void setRequired(Boolean required) { this.required = required; }

        public String getPlaceholder() { return placeholder; }
        public void setPlaceholder(String placeholder) { this.placeholder = placeholder; }

        public List<String> getOptions() { return options; }
        public void setOptions(List<String> options) { this.options = options; }

        public String getExample() { return example; }
        public void setExample(String example) { this.example = example; }
    }
}
