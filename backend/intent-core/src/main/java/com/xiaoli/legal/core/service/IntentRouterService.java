package com.xiaoli.legal.core.service;

import com.xiaoli.legal.core.IntentType;
import com.xiaoli.legal.core.AIEngineType;
import com.xiaoli.legal.core.model.EngineSelectionResult;
import com.xiaoli.legal.core.model.IntentResult;
import com.xiaoli.legal.core.model.UserQuery;

/**
 * 统一意图路由服务
 * 整合意图识别、引擎路由和响应生成
 */
public interface IntentRouterService {

    /**
     * 处理用户查询（完整流程）
     *
     * @param query 用户查询
     * @return 处理结果
     */
    QueryResponse processQuery(UserQuery query);

    /**
     * 仅识别意图
     *
     * @param query 用户查询
     * @return 意图识别结果
     */
    IntentResult recognizeOnly(UserQuery query);

    /**
     * 仅路由到引擎
     *
     * @param query 用户查询
     * @param intentResult 意图识别结果
     * @return 引擎选择结果
     */
    EngineSelectionResult routeOnly(UserQuery query, IntentResult intentResult);

    /**
     * 处理结果
     */
    class QueryResponse {
        private String queryText;
        private IntentResult intentResult;
        private EngineSelectionResult engineResult;
        private String response;
        private Long responseTime;
        private Boolean success;
        private String errorMessage;

        public String getQueryText() { return queryText; }
        public void setQueryText(String queryText) { this.queryText = queryText; }
        public IntentResult getIntentResult() { return intentResult; }
        public void setIntentResult(IntentResult intentResult) { this.intentResult = intentResult; }
        public EngineSelectionResult getEngineResult() { return engineResult; }
        public void setEngineResult(EngineSelectionResult engineResult) { this.engineResult = engineResult; }
        public String getResponse() { return response; }
        public void setResponse(String response) { this.response = response; }
        public Long getResponseTime() { return responseTime; }
        public void setResponseTime(Long responseTime) { this.responseTime = responseTime; }
        public Boolean getSuccess() { return success; }
        public void setSuccess(Boolean success) { this.success = success; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

        public QueryResponse() {}
        public QueryResponse(String queryText, IntentResult intentResult, EngineSelectionResult engineResult,
                String response, Long responseTime, Boolean success, String errorMessage) {
            this.queryText = queryText;
            this.intentResult = intentResult;
            this.engineResult = engineResult;
            this.response = response;
            this.responseTime = responseTime;
            this.success = success;
            this.errorMessage = errorMessage;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String queryText;
            private IntentResult intentResult;
            private EngineSelectionResult engineResult;
            private String response;
            private Long responseTime;
            private Boolean success;
            private String errorMessage;

            public Builder queryText(String queryText) { this.queryText = queryText; return this; }
            public Builder intentResult(IntentResult intentResult) { this.intentResult = intentResult; return this; }
            public Builder engineResult(EngineSelectionResult engineResult) { this.engineResult = engineResult; return this; }
            public Builder response(String response) { this.response = response; return this; }
            public Builder responseTime(Long responseTime) { this.responseTime = responseTime; return this; }
            public Builder success(Boolean success) { this.success = success; return this; }
            public Builder errorMessage(String errorMessage) { this.errorMessage = errorMessage; return this; }

            public QueryResponse build() {
                return new QueryResponse(queryText, intentResult, engineResult, response, responseTime, success, errorMessage);
            }
        }
    }
}
