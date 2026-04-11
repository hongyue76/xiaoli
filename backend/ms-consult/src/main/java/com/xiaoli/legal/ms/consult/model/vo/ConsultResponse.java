package com.xiaoli.legal.ms.consult.model.vo;

import com.xiaoli.legal.ms.consult.service.CaseSearchClient.SimilarCase;
import java.util.List;

/**
 * 咨询响应
 */
public class ConsultResponse {

    /**
     * 会话ID
     */
    private Long conversationId;

    /**
     * 回复内容
     */
    private String answer;

    /**
     * 引用法律条文
     */
    private String legalReference;

    /**
     * 引用案例
     */
    private String caseReference;

    /**
     * 相关建议
     */
    private String suggestions;

    /**
     * 会话标题
     */
    private String title;

    /**
     * 相似案例列表
     */
    private List<SimilarCase> similarCases;

    // Getters and Setters
    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public String getLegalReference() { return legalReference; }
    public void setLegalReference(String legalReference) { this.legalReference = legalReference; }

    public String getCaseReference() { return caseReference; }
    public void setCaseReference(String caseReference) { this.caseReference = caseReference; }

    public String getSuggestions() { return suggestions; }
    public void setSuggestions(String suggestions) { this.suggestions = suggestions; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<SimilarCase> getSimilarCases() { return similarCases; }
    public void setSimilarCases(List<SimilarCase> similarCases) { this.similarCases = similarCases; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long conversationId;
        private String answer;
        private String legalReference;
        private String caseReference;
        private String suggestions;
        private String title;
        private List<SimilarCase> similarCases;

        public Builder conversationId(Long conversationId) { this.conversationId = conversationId; return this; }
        public Builder answer(String answer) { this.answer = answer; return this; }
        public Builder legalReference(String legalReference) { this.legalReference = legalReference; return this; }
        public Builder caseReference(String caseReference) { this.caseReference = caseReference; return this; }
        public Builder suggestions(String suggestions) { this.suggestions = suggestions; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder similarCases(List<SimilarCase> similarCases) { this.similarCases = similarCases; return this; }

        public ConsultResponse build() {
            ConsultResponse response = new ConsultResponse();
            response.setConversationId(conversationId);
            response.setAnswer(answer);
            response.setLegalReference(legalReference);
            response.setCaseReference(caseReference);
            response.setSuggestions(suggestions);
            response.setTitle(title);
            response.setSimilarCases(similarCases);
            return response;
        }
    }
}
