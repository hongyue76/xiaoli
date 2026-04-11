package com.xiaoli.legal.ms.document.model.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * 文书生成请求
 */
public class GenerateDocumentRequest {

    /**
     * 模板ID
     */
    @NotNull(message = "模板ID不能为空")
    private Long templateId;

    /**
     * 案件ID(可选)
     */
    private Long caseId;

    /**
     * 文书变量数据
     */
    @NotNull(message = "文书数据不能为空")
    private Map<String, Object> data;

    /**
     * 是否使用AI智能填充
     */
    private Boolean aiAssist = false;

    /**
     * AI辅助提示
     */
    private String aiPrompt;

    /**
     * 输出格式: MARKDOWN, PDF
     */
    private String outputFormat = "MARKDOWN";

    // Getters and Setters
    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }

    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }

    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }

    public Boolean getAiAssist() { return aiAssist; }
    public void setAiAssist(Boolean aiAssist) { this.aiAssist = aiAssist; }

    public String getAiPrompt() { return aiPrompt; }
    public void setAiPrompt(String aiPrompt) { this.aiPrompt = aiPrompt; }

    public String getOutputFormat() { return outputFormat; }
    public void setOutputFormat(String outputFormat) { this.outputFormat = outputFormat; }
}
