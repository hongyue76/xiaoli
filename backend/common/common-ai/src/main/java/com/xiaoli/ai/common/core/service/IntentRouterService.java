package com.xiaoli.ai.common.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaoli.ai.common.core.model.Intent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 意图路由服务
 */
@Service
public class IntentRouterService {

    private static final Logger log = LoggerFactory.getLogger(IntentRouterService.class);

    @Autowired
    private DeepSeekService deepSeekService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 分析用户意图
     */
    public Intent analyzeIntent(String question) {
        log.info("开始分析用户意图: {}", question);
        
        try {
            Intent intent = deepSeekService.analyzeIntent(question);
            log.info("意图分析完成: type={}, confidence={}", 
                    intent.getType(), intent.getConfidence());
            return intent;
        } catch (Exception e) {
            log.error("意图分析失败: {}", e.getMessage(), e);
            // 返回默认意图
            return Intent.builder()
                    .type(Intent.IntentType.GENERAL)
                    .needSearch(false)
                    .confidence(0.5)
                    .reason("分析失败，使用默认意图")
                    .originalQuestion(question)
                    .build();
        }
    }

    /**
     * 意图路由 - 根据用户问题判断处理方式
     */
    public IntentRouteResult routeIntent(String question) {
        log.info("开始路由分析用户问题: {}", question);

        try {
            // 1. 调用 DeepSeek 分析意图
            Intent intent = deepSeekService.analyzeIntent(question);
            log.info("意图分析结果: type={}, needSearch={}, confidence={}", 
                    intent.getType(), intent.requiresSearch(), intent.getConfidence());

            // 2. 根据意图决定处理方式
            IntentRouteResult result = IntentRouteResult.builder()
                    .intent(intent)
                    .success(true)
                    .build();

            if (intent.isProfessional()) {
                // 专业法律问题 - 需要检索
                log.info("检测到专业法律问题，执行检索流程");
                result.setProcessType(ProcessType.PROFESSIONAL_WITH_SEARCH);
                // 执行检索（调用得理API）
                String searchResults = performLegalSearch(question);
                result.setSearchResults(searchResults);
            } else {
                // 通用问题 - 直接回答
                log.info("检测到通用问题，执行直接回答流程");
                result.setProcessType(ProcessType.GENERAL);
                // 生成回答
                String answer = deepSeekService.generateAnswer(question);
                result.setDirectAnswer(answer);
            }

            return result;

        } catch (Exception e) {
            log.error("意图路由失败: {}", e.getMessage(), e);
            return IntentRouteResult.builder()
                    .intent(null)
                    .success(false)
                    .errorMessage(e.getMessage())
                    .processType(ProcessType.ERROR)
                    .build();
        }
    }

    /**
     * 执行法律检索（调用得理API）
     */
    private String performLegalSearch(String question) {
        log.info("执行法律检索: {}", question);
        
        try {
            // TODO: 替换为实际的得理API调用
            // 这里模拟检索结果，实际应该调用得理API
            String mockSearchResults = """
                    {
                      "cases": [
                        {
                          "title": "劳动合同纠纷案例",
                          "caseNo": "(2024)京0105民初12345号",
                          "court": "北京市朝阳区人民法院",
                          "summary": "本案涉及解除劳动合同的合法性问题...",
                          "judgmentResult": "判决用人单位支付违法解除劳动合同赔偿金"
                        },
                        {
                          "title": "劳动合同法相关条文",
                          "lawTitle": "劳动合同法",
                          "article": "第三十九条",
                          "content": "劳动者有下列情形之一的，用人单位可以解除劳动合同..."
                        }
                      ],
                      "statutes": [
                        {
                          "title": "劳动合同法",
                          "articles": [
                            {
                              "article": "第三十九条",
                              "content": "劳动者有下列情形之一的，用人单位可以解除劳动合同..."
                            }
                          ]
                        }
                      ]
                    }
                    """;
            
            log.info("法律检索完成");
            return mockSearchResults;

        } catch (Exception e) {
            log.error("法律检索失败: {}", e.getMessage(), e);
            return "{\"error\": \"法律检索失败\"}";
        }
    }

    /**
     * 流程处理类型
     */
    public enum ProcessType {
        /** 专业法律问题 + 检索 + AI生成 */
        PROFESSIONAL_WITH_SEARCH,

        /** 通用问题 + 直接AI回答 */
        GENERAL,

        /** 错误 */
        ERROR
    }

    /**
     * 意图路由结果
     */
    public static class IntentRouteResult {
        private Intent intent;
        private ProcessType processType;
        private boolean success;
        private String searchResults;
        private String directAnswer;
        private String errorMessage;

        public Intent getIntent() { return intent; }
        public void setIntent(Intent intent) { this.intent = intent; }
        public ProcessType getProcessType() { return processType; }
        public void setProcessType(ProcessType processType) { this.processType = processType; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getSearchResults() { return searchResults; }
        public void setSearchResults(String searchResults) { this.searchResults = searchResults; }
        public String getDirectAnswer() { return directAnswer; }
        public void setDirectAnswer(String directAnswer) { this.directAnswer = directAnswer; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

        public boolean requiresSearch() {
            return processType == ProcessType.PROFESSIONAL_WITH_SEARCH;
        }

        public String getFinalAnswer() {
            if (success && processType == ProcessType.PROFESSIONAL_WITH_SEARCH) {
                return searchResults;
            }
            return directAnswer;
        }

        public IntentRouteResult() {}
        public IntentRouteResult(Intent intent, ProcessType processType, boolean success,
                String searchResults, String directAnswer, String errorMessage) {
            this.intent = intent;
            this.processType = processType;
            this.success = success;
            this.searchResults = searchResults;
            this.directAnswer = directAnswer;
            this.errorMessage = errorMessage;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private Intent intent;
            private ProcessType processType;
            private boolean success;
            private String searchResults;
            private String directAnswer;
            private String errorMessage;

            public Builder intent(Intent intent) { this.intent = intent; return this; }
            public Builder processType(ProcessType processType) { this.processType = processType; return this; }
            public Builder success(boolean success) { this.success = success; return this; }
            public Builder searchResults(String searchResults) { this.searchResults = searchResults; return this; }
            public Builder directAnswer(String directAnswer) { this.directAnswer = directAnswer; return this; }
            public Builder errorMessage(String errorMessage) { this.errorMessage = errorMessage; return this; }

            public IntentRouteResult build() {
                return new IntentRouteResult(intent, processType, success, searchResults, directAnswer, errorMessage);
            }
        }
    }
}
