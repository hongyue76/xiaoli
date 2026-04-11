package com.xiaoli.legal.ms.consult.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaoli.legal.common.ai.service.DelilegalService;
import com.xiaoli.legal.common.core.domain.PageResult;
import com.xiaoli.legal.common.core.domain.Result;
import com.xiaoli.legal.ms.consult.model.dto.ChatRequest;
import com.xiaoli.legal.ms.consult.model.dto.ChatResponse;
import com.xiaoli.legal.ms.consult.model.entity.ConsultConversation;
import com.xiaoli.legal.ms.consult.model.entity.ConsultMessage;
import com.xiaoli.legal.ms.consult.model.vo.ConsultRecordVO;
import com.xiaoli.legal.ms.consult.model.vo.ConsultResponse;
import com.xiaoli.legal.ms.consult.service.CaseSearchClient.SimilarCase;
import com.xiaoli.legal.ms.consult.service.ConsultService;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 法律咨询接口
 */
@RestController
@RequestMapping("/api/consult")
public class ConsultController {

    private static final Logger log = LoggerFactory.getLogger(ConsultController.class);
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final ConsultService consultService;
    private final DelilegalService delilegalService;
    private final ObjectMapper objectMapper;

    @Value("${deepseek.api.url:https://api.deepseek.com/v1}")
    private String deepseekUrl;

    @Value("${deepseek.api.key}")
    private String deepseekApiKey;

    @Value("${deepseek.api.model:deepseek-chat}")
    private String deepseekModel;

    private final OkHttpClient httpClient;

    public ConsultController(ConsultService consultService,
                           DelilegalService delilegalService,
                           ObjectMapper objectMapper) {
        this.consultService = consultService;
        this.delilegalService = delilegalService;
        this.objectMapper = objectMapper;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 测试端点 - 直接调用DeepSeek
     */
    @GetMapping("/test")
    public Result<String> test() {
        log.info("DeepSeek URL: {}", deepseekUrl);
        log.info("DeepSeek API Key: {}", deepseekApiKey != null ? "已配置" : "未配置");
        return Result.success("DeepSeek配置测试: URL=" + deepseekUrl + ", Key已配置=" + (deepseekApiKey != null));
    }

    /**
     * 智能法律咨询
     */
    @PostMapping("/chat")
    public Result<ConsultResponse> chat(@RequestBody @Validated ChatRequest request) {
        log.info("收到法律咨询请求: {}", request.getMessages());

        // 获取用户问题
        String userQuestion = "";
        if (request.getMessages() != null) {
            for (ChatRequest.ChatMessage msg : request.getMessages()) {
                if ("user".equals(msg.getRole())) {
                    userQuestion = msg.getContent();
                    break;
                }
            }
        }

        // 检索相似案例（先检索，作为上下文传给AI）
        List<SimilarCase> similarCases = new ArrayList<>();
        String caseContext = "";
        try {
            String casesJson = delilegalService.searchCases(
                    List.of(userQuestion),
                    userQuestion,
                    null,
                    null
            );
            similarCases = parseSimilarCases(casesJson);
            log.info("得理法搜案例检索完成，返回{}个案例", similarCases.size());
            
            // 构建案例上下文
            if (!similarCases.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append("【相似案例参考】（以下是从法律数据库中检索到的真实案例，请结合这些案例回答用户问题）：\n\n");
                for (int i = 0; i < Math.min(similarCases.size(), 5); i++) {
                    SimilarCase c = similarCases.get(i);
                    sb.append("案例").append(i + 1).append("：")
                      .append(c.getTitle()).append("\n")
                      .append("法院：").append(c.getCourt()).append("\n")
                      .append("案号：").append(c.getCaseNo()).append("\n")
                      .append("案件类型：").append(c.getCaseType()).append("\n")
                      .append("案情摘要：").append(c.getSummary()).append("\n")
                      .append("裁判结果：").append(c.getJudgmentResult()).append("\n")
                      .append("法律依据：").append(c.getLegalBasis()).append("\n\n");
                }
                caseContext = sb.toString();
            }
        } catch (Exception e) {
            log.warn("案例检索失败: {}", e.getMessage());
        }

        // 调用DeepSeek AI服务获取回答（传入案例上下文）
        String answer = callDeepSeekWithContext(userQuestion, caseContext);

        // 构建响应
        ConsultResponse consultResponse = ConsultResponse.builder()
                .answer(answer)
                .similarCases(similarCases)
                .title(userQuestion.length() > 50 ? userQuestion.substring(0, 50) + "..." : userQuestion)
                .build();

        // 保存会话记录
        ChatResponse chatResponse = ChatResponse.builder()
                .choices(List.of(ChatResponse.Choice.builder()
                        .message(ChatRequest.ChatMessage.builder()
                                .role("assistant")
                                .content(answer)
                                .build())
                        .build()))
                .build();
        ConsultResponse savedResponse = consultService.saveConsultRecord(request, chatResponse);
        
        consultResponse.setConversationId(savedResponse.getConversationId());

        return Result.success(consultResponse);
    }

    /**
     * 简单法律问答
     */
    @GetMapping("/ask")
    public Result<String> ask(@RequestParam("question") String question) {
        log.info("收到法律问题: {}", question);
        String answer = callDeepSeek(question);
        return Result.success(answer);
    }

    /**
     * 带案例上下文的DeepSeek调用
     */
    private String callDeepSeekWithContext(String question, String caseContext) {
        try {
            String systemPrompt;
            if (caseContext != null && !caseContext.isEmpty()) {
                systemPrompt = "你是一位专业的法律顾问助手，专门为用户提供法律咨询服务。\n\n" +
                        "【重要】下面的【相似案例参考】部分是从法律数据库检索到的真实案例，请务必：\n" +
                        "1. 仔细阅读这些案例的案情和裁判结果\n" +
                        "2. 结合案例中的法律依据进行分析\n" +
                        "3. 参考类似案例的裁判思路\n" +
                        "4. 给出针对用户问题的具体法律建议\n\n" +
                        "请用专业、清晰、易懂的语言回答，包含：\n" +
                        "1. 法律依据（引用相关法律条文）\n" +
                        "2. 法律分析（结合参考案例说明）\n" +
                        "3. 建议措施\n" +
                        "4. 注意事项\n\n" +
                        caseContext;
            } else {
                systemPrompt = "你是一位专业的法律顾问助手，专门为用户提供法律咨询服务。" +
                        "请用专业、清晰、易懂的语言回答用户的法律问题。" +
                        "回答应该包含：1.法律依据 2.法律分析 3.建议措施 4.注意事项。" +
                        "如果问题涉及具体案件，建议咨询专业律师。";
            }

            // 使用JSONObject构建请求体，避免格式化问题
            com.alibaba.fastjson2.JSONObject requestBody = new com.alibaba.fastjson2.JSONObject();
            requestBody.put("model", deepseekModel);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 3000);
            
            com.alibaba.fastjson2.JSONArray messages = new com.alibaba.fastjson2.JSONArray();
            com.alibaba.fastjson2.JSONObject systemMsg = new com.alibaba.fastjson2.JSONObject();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);
            messages.add(systemMsg);
            
            com.alibaba.fastjson2.JSONObject userMsg = new com.alibaba.fastjson2.JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", question);
            messages.add(userMsg);
            
            requestBody.put("messages", messages);

            Request httpRequest = new Request.Builder()
                    .url(deepseekUrl + "/chat/completions")
                    .addHeader("Authorization", "Bearer " + deepseekApiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(okhttp3.RequestBody.create(requestBody.toJSONString(), JSON))
                    .build();

            log.info("DeepSeek 请求体: {}", requestBody.toJSONString().substring(0, Math.min(500, requestBody.toJSONString().length())));

            try (Response response = httpClient.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "";
                    log.error("DeepSeek API 调用失败: {} - {}, body: {}", response.code(), response.message(), errorBody);
                    return "抱歉，AI服务暂时不可用。请稍后再试。错误: " + response.code();
                }

                String responseBody = response.body() != null ? response.body().string() : "";
                log.info("DeepSeek 响应: {}", responseBody);

                // 解析响应
                var root = objectMapper.readTree(responseBody);
                String content = root.at("/choices/0/message/content").asText();
                return content;
            }
        } catch (Exception e) {
            log.error("DeepSeek API 调用异常", e);
            return "抱歉，AI服务暂时不可用。请稍后再试。错误: " + e.getMessage();
        }
    }

    /**
     * 直接调用DeepSeek API（保留兼容性）
     */
    private String callDeepSeek(String question) {
        try {
            String systemPrompt = "你是一位专业的法律顾问助手，专门为用户提供法律咨询服务。" +
                    "请用专业、清晰、易懂的语言回答用户的法律问题。" +
                    "回答应该包含：1.法律依据 2.法律分析 3.建议措施 4.注意事项。" +
                    "如果问题涉及具体案件，建议咨询专业律师。";

            String requestBody = String.format("""
                {
                    "model": "%s",
                    "messages": [
                        {"role": "system", "content": "%s"},
                        {"role": "user", "content": "%s"}
                    ],
                    "temperature": 0.7,
                    "max_tokens": 2000
                }
                """, deepseekModel, systemPrompt.replace("\"", "\\\""), question.replace("\"", "\\\""));

            Request httpRequest = new Request.Builder()
                    .url(deepseekUrl + "/chat/completions")
                    .addHeader("Authorization", "Bearer " + deepseekApiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(okhttp3.RequestBody.create(requestBody, JSON))
                    .build();

            try (Response response = httpClient.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    log.error("DeepSeek API 调用失败: {} - {}", response.code(), response.message());
                    return "抱歉，AI服务暂时不可用。请稍后再试。错误: " + response.code();
                }

                String responseBody = response.body() != null ? response.body().string() : "";
                log.info("DeepSeek 响应: {}", responseBody);

                // 解析响应
                var root = objectMapper.readTree(responseBody);
                String content = root.at("/choices/0/message/content").asText();
                return content;
            }
        } catch (Exception e) {
            log.error("DeepSeek API 调用异常", e);
            return "抱歉，AI服务暂时不可用。请稍后再试。错误: " + e.getMessage();
        }
    }

    /**
     * 解析相似案例JSON（得理法搜API响应格式）
     */
    private List<SimilarCase> parseSimilarCases(String json) {
        List<SimilarCase> cases = new ArrayList<>();
        if (json == null || json.isEmpty()) {
            return cases;
        }
        try {
            var root = objectMapper.readTree(json);
            // 得理法搜响应格式: body.data[]
            var data = root.path("body").path("data");
            if (data.isArray()) {
                for (var node : data) {
                    SimilarCase sc = new SimilarCase();
                    // 得理法搜字段映射
                    sc.setId((long) node.path("id").asText("").hashCode());
                    sc.setTitle(node.path("title").asText(""));
                    sc.setCaseNo(node.path("caseNumber").asText(""));
                    sc.setCaseType(node.path("caseType").asText(""));
                    sc.setCourt(node.path("court").asText(""));
                    sc.setJudgmentDate(node.path("judgementDate").asText(""));
                    sc.setSummary(node.path("content").asText("").substring(0, Math.min(200, node.path("content").asText("").length())));
                    sc.setJudgmentResult("");
                    sc.setLegalBasis("");
                    sc.setScore(1.0);
                    cases.add(sc);
                }
            }
        } catch (Exception e) {
            log.warn("解析相似案例JSON失败: {}", e.getMessage());
        }
        return cases;
    }

    /**
     * 获取咨询记录列表
     */
    @GetMapping("/history")
    public Result<PageResult<ConsultRecordVO>> getHistory(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        PageResult<ConsultRecordVO> result = consultService.getConsultHistory(userId, current, size);
        return Result.success(result);
    }

    /**
     * 获取会话详情
     */
    @GetMapping("/conversation/{id}")
    public Result<ConsultRecordVO> getConversation(@PathVariable Long id) {
        ConsultRecordVO vo = consultService.getConversationDetail(id);
        return Result.success(vo);
    }

    /**
     * 获取会话消息列表
     */
    @GetMapping("/messages/{conversationId}")
    public Result<List<ConsultMessage>> getMessages(@PathVariable Long conversationId) {
        List<ConsultMessage> messages = consultService.getConversationMessages(conversationId);
        return Result.success(messages);
    }

    /**
     * 新建咨询会话
     */
    @PostMapping("/conversation")
    public Result<ConsultConversation> createConversation(@RequestBody @Validated ConsultConversation conversation) {
        ConsultConversation result = consultService.createConversation(conversation);
        return Result.success(result);
    }

    /**
     * 结束会话
     */
    @PutMapping("/conversation/{id}/close")
    public Result<Void> closeConversation(@PathVariable Long id) {
        consultService.closeConversation(id);
        return Result.success();
    }

    /**
     * 获取咨询分类
     */
    @GetMapping("/categories")
    public Result<List<String>> getCategories() {
        List<String> categories = List.of(
                "GENERAL", "DIVORCE", "CONTRACT", "LABOR", "INHERITANCE",
                "CRIMINAL", "ADMINISTRATIVE", "PROPERTY", "TORT", "COMPANY"
        );
        return Result.success(categories);
    }
}
