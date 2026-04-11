package com.xiaoli.intent.core.controller;

import com.xiaoli.ai.common.core.model.Intent;
import com.xiaoli.ai.common.core.service.DeepSeekService;
import com.xiaoli.ai.common.core.service.IntentRouterService;
import com.xiaoli.legal.common.core.domain.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 意图路由控制器
 */
@RestController
@RequestMapping("/intent-router")
@CrossOrigin(origins = "*")
public class IntentRouterController {

    private static final Logger log = LoggerFactory.getLogger(IntentRouterController.class);

    @Autowired
    private IntentRouterService intentRouterService;

    @Autowired
    private DeepSeekService deepSeekService;

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        return Result.success(Map.of(
            "status", "UP",
            "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * 分析用户意图
     */
    @PostMapping("/analyze")
    public Result<Intent> analyzeIntent(@RequestBody AnalyzeRequest request) {
        log.info("收到意图分析请求: {}", request.getQuestion());

        try {
            Intent intent = intentRouterService.analyzeIntent(request.getQuestion());

            log.info("意图分析完成: type={}, confidence={}",
                    intent.getType(), intent.getConfidence());

            return Result.success(intent);

        } catch (Exception e) {
            log.error("意图分析失败: {}", e.getMessage(), e);
            return Result.fail("意图分析失败: " + e.getMessage());
        }
    }

    /**
     * 智能问答 - 根据意图自动路由
     */
    @PostMapping("/chat")
    public Result<ChatResponse> chat(@RequestBody ChatRequest request) {
        log.info("收到智能问答请求: {}", request.getQuestion());
        long startTime = System.currentTimeMillis();

        try {
            // 1. 分析意图
            Intent intent = intentRouterService.analyzeIntent(request.getQuestion());
            
            // 2. 根据意图路由处理
            String answer;
            String searchResults = null;
            String processType;

            if (intent.isNeedSearch()) {
                // 专业问题：需要检索
                processType = "PROFESSIONAL_WITH_SEARCH";
                
                // 这里应该调用得理API检索
                // 暂时模拟检索结果
                searchResults = simulateSearchResults(request.getQuestion());
                
                // 结合检索结果生成回答
                answer = deepSeekService.chat(
                    "请基于以下检索结果回答用户问题：\n\n" + 
                    searchResults + "\n\n用户问题：" + request.getQuestion(),
                    "你是一个专业的法律助手"
                );
            } else {
                // 通用问题：直接回答
                processType = "GENERAL_WITHOUT_SEARCH";
                answer = deepSeekService.chat(
                    request.getQuestion(),
                    "你是一个友好的助手"
                );
            }

            ChatResponse response = ChatResponse.builder()
                    .answer(answer)
                    .intentType(intent.getType().name())
                    .processType(processType)
                    .searchResults(searchResults)
                    .duration(System.currentTimeMillis() - startTime)
                    .build();

            log.info("智能问答完成: processType={}, duration={}ms", 
                    processType, response.getDuration());

            return Result.success(response);

        } catch (Exception e) {
            log.error("智能问答处理失败: {}", e.getMessage(), e);
            return Result.fail("智能问答处理失败: " + e.getMessage());
        }
    }

    /**
     * 路由请求 - 根据用户问题智能路由到不同的处理方式
     */
    @PostMapping("/route")
    public Result<IntentRouteResponse> routeQuestion(@RequestBody RouteRequest request) {
        log.info("收到意图路由请求: {}", request.getQuestion());

        try {
            IntentRouterService.IntentRouteResult result = intentRouterService.routeIntent(request.getQuestion());
            
            IntentRouteResponse response = IntentRouteResponse.builder()
                    .intentType(result.getIntent() != null ? result.getIntent().getType().name() : null)
                    .processType(result.getProcessType().name())
                    .answer(result.getFinalAnswer())
                    .searchResults(result.requiresSearch() ? result.getSearchResults() : null)
                    .build();

            log.info("意图路由完成: processType={}, intentType={}", 
                    result.getProcessType(), result.getIntent() != null ? result.getIntent().getType() : null);

            return Result.success(response);

        } catch (Exception e) {
            log.error("意图路由处理失败: {}", e.getMessage(), e);
            return Result.fail("意图路由处理失败: " + e.getMessage());
        }
    }

    /**
     * 模拟检索结果
     */
    private String simulateSearchResults(String question) {
        return """
        [
          {
            "title": "劳动合同法 第十九条",
            "content": "劳动合同期限三个月以上不满一年的，试用期不得超过一个月；劳动合同期限一年以上不满三年的，试用期不得超过二个月；三年以上固定期限和无固定期限的劳动合同，试用期不得超过六个月。"
          },
          {
            "title": "劳动合同法 第八十三条",
            "content": "用人单位违反本法规定与劳动者约定试用期的，由劳动行政部门责令改正；违法约定的试用期已经履行的，由用人单位以劳动者试用期满月工资为标准，按已经履行的超过法定试用期的期间向劳动者支付赔偿金。"
          }
        ]
        """;
    }

    /**
     * 意图分析请求体
     */
    public static class AnalyzeRequest {
        private String question;

        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }

        public AnalyzeRequest() {}
        public AnalyzeRequest(String question) { this.question = question; }
    }

    /**
     * 智能问答请求体
     */
    public static class ChatRequest {
        private String question;
        private Long userId;
        private String sessionId;

        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }

        public ChatRequest() {}
        public ChatRequest(String question, Long userId, String sessionId) {
            this.question = question;
            this.userId = userId;
            this.sessionId = sessionId;
        }
    }

    /**
     * 智能问答响应体
     */
    public static class ChatResponse {
        private String answer;
        private String intentType;
        private String processType;
        private String searchResults;
        private Long duration;

        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
        public String getIntentType() { return intentType; }
        public void setIntentType(String intentType) { this.intentType = intentType; }
        public String getProcessType() { return processType; }
        public void setProcessType(String processType) { this.processType = processType; }
        public String getSearchResults() { return searchResults; }
        public void setSearchResults(String searchResults) { this.searchResults = searchResults; }
        public Long getDuration() { return duration; }
        public void setDuration(Long duration) { this.duration = duration; }

        public ChatResponse() {}
        public ChatResponse(String answer, String intentType, String processType, String searchResults, Long duration) {
            this.answer = answer;
            this.intentType = intentType;
            this.processType = processType;
            this.searchResults = searchResults;
            this.duration = duration;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String answer;
            private String intentType;
            private String processType;
            private String searchResults;
            private Long duration;

            public Builder answer(String answer) { this.answer = answer; return this; }
            public Builder intentType(String intentType) { this.intentType = intentType; return this; }
            public Builder processType(String processType) { this.processType = processType; return this; }
            public Builder searchResults(String searchResults) { this.searchResults = searchResults; return this; }
            public Builder duration(Long duration) { this.duration = duration; return this; }

            public ChatResponse build() {
                return new ChatResponse(answer, intentType, processType, searchResults, duration);
            }
        }
    }

    /**
     * 路由请求体
     */
    public static class RouteRequest {
        private String question;
        private Map<String, Object> context;

        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        public Map<String, Object> getContext() { return context; }
        public void setContext(Map<String, Object> context) { this.context = context; }

        public RouteRequest() {}
        public RouteRequest(String question, Map<String, Object> context) {
            this.question = question;
            this.context = context;
        }
    }

    /**
     * 路由响应体
     */
    public static class IntentRouteResponse {
        private String intentType;
        private String processType;
        private String answer;
        private String searchResults;
        private Long duration;

        public String getIntentType() { return intentType; }
        public void setIntentType(String intentType) { this.intentType = intentType; }
        public String getProcessType() { return processType; }
        public void setProcessType(String processType) { this.processType = processType; }
        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
        public String getSearchResults() { return searchResults; }
        public void setSearchResults(String searchResults) { this.searchResults = searchResults; }
        public Long getDuration() { return duration; }
        public void setDuration(Long duration) { this.duration = duration; }

        public IntentRouteResponse() {}
        public IntentRouteResponse(String intentType, String processType, String answer, String searchResults, Long duration) {
            this.intentType = intentType;
            this.processType = processType;
            this.answer = answer;
            this.searchResults = searchResults;
            this.duration = duration;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String intentType;
            private String processType;
            private String answer;
            private String searchResults;
            private Long duration;

            public Builder intentType(String intentType) { this.intentType = intentType; return this; }
            public Builder processType(String processType) { this.processType = processType; return this; }
            public Builder answer(String answer) { this.answer = answer; return this; }
            public Builder searchResults(String searchResults) { this.searchResults = searchResults; return this; }
            public Builder duration(Long duration) { this.duration = duration; return this; }

            public IntentRouteResponse build() {
                return new IntentRouteResponse(intentType, processType, answer, searchResults, duration);
            }
        }
    }
}
