package com.xiaoli.legal.ms.document.model.dto;

/**
 * 文书生成响应
 */
public class GenerateDocumentResponse {

    /**
     * 文书ID
     */
    private Long documentId;

    /**
     * 文书标题
     */
    private String title;

    /**
     * 文书内容
     */
    private String content;

    /**
     * PDF文件URL
     */
    private String pdfUrl;

    /**
     * AI建议
     */
    private String aiSuggestions;

    /**
     * 风险提示
     */
    private String riskWarning;

    // Getters and Setters
    public Long getDocumentId() { return documentId; }
    public void setDocumentId(Long documentId) { this.documentId = documentId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }

    public String getAiSuggestions() { return aiSuggestions; }
    public void setAiSuggestions(String aiSuggestions) { this.aiSuggestions = aiSuggestions; }

    public String getRiskWarning() { return riskWarning; }
    public void setRiskWarning(String riskWarning) { this.riskWarning = riskWarning; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long documentId;
        private String title;
        private String content;
        private String pdfUrl;
        private String aiSuggestions;
        private String riskWarning;

        public Builder documentId(Long documentId) { this.documentId = documentId; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder content(String content) { this.content = content; return this; }
        public Builder pdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; return this; }
        public Builder aiSuggestions(String aiSuggestions) { this.aiSuggestions = aiSuggestions; return this; }
        public Builder riskWarning(String riskWarning) { this.riskWarning = riskWarning; return this; }

        public GenerateDocumentResponse build() {
            GenerateDocumentResponse response = new GenerateDocumentResponse();
            response.setDocumentId(documentId);
            response.setTitle(title);
            response.setContent(content);
            response.setPdfUrl(pdfUrl);
            response.setAiSuggestions(aiSuggestions);
            response.setRiskWarning(riskWarning);
            return response;
        }
    }
}
