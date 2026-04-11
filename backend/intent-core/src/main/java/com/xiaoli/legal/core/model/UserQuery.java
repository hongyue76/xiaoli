package com.xiaoli.legal.core.model;

import com.xiaoli.legal.core.IntentType;
import java.util.Map;

/**
 * 用户查询请求
 */
public class UserQuery {

    /**
     * 用户输入的文本
     */
    private String queryText;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 上下文信息（历史对话等）
     */
    private Map<String, Object> context;

    /**
     * 预期意图（可选，用户手动选择）
     */
    private IntentType expectedIntent;

    /**
     * 强制使用指定意图（不进行意图识别）
     */
    private Boolean forceIntent;

    // Getters and Setters
    public String getQueryText() { return queryText; }
    public void setQueryText(String queryText) { this.queryText = queryText; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context; }

    public IntentType getExpectedIntent() { return expectedIntent; }
    public void setExpectedIntent(IntentType expectedIntent) { this.expectedIntent = expectedIntent; }

    public Boolean getForceIntent() { return forceIntent; }
    public void setForceIntent(Boolean forceIntent) { this.forceIntent = forceIntent; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String queryText;
        private String sessionId;
        private String userId;
        private Map<String, Object> context;
        private IntentType expectedIntent;
        private Boolean forceIntent;

        public Builder queryText(String queryText) { this.queryText = queryText; return this; }
        public Builder sessionId(String sessionId) { this.sessionId = sessionId; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder context(Map<String, Object> context) { this.context = context; return this; }
        public Builder expectedIntent(IntentType expectedIntent) { this.expectedIntent = expectedIntent; return this; }
        public Builder forceIntent(Boolean forceIntent) { this.forceIntent = forceIntent; return this; }

        public UserQuery build() {
            UserQuery query = new UserQuery();
            query.setQueryText(queryText);
            query.setSessionId(sessionId);
            query.setUserId(userId);
            query.setContext(context);
            query.setExpectedIntent(expectedIntent);
            query.setForceIntent(forceIntent);
            return query;
        }
    }
}
