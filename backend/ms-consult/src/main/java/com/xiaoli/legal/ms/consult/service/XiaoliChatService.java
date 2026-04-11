package com.xiaoli.legal.ms.consult.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaoli.legal.common.core.exception.BusinessException;
import com.xiaoli.legal.ms.consult.config.CaseSearchConfig;
import com.xiaoli.legal.ms.consult.config.XiaoliConfig;
import com.xiaoli.legal.ms.consult.model.dto.ChatRequest;
import com.xiaoli.legal.ms.consult.model.dto.ChatResponse;
import com.xiaoli.legal.ms.consult.service.CaseSearchClient.SimilarCase;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 小理AI对话服务
 */
@Service
public class XiaoliChatService {

    private static final Logger log = LoggerFactory.getLogger(XiaoliChatService.class);

    private final XiaoliConfig xiaoliConfig;
    private final CaseSearchClient caseSearchClient;
    private final CaseSearchConfig caseSearchConfig;
    private final ObjectMapper objectMapper;

    public XiaoliChatService(XiaoliConfig xiaoliConfig, CaseSearchClient caseSearchClient,
                            CaseSearchConfig caseSearchConfig, ObjectMapper objectMapper) {
        this.xiaoliConfig = xiaoliConfig;
        this.caseSearchClient = caseSearchClient;
        this.caseSearchConfig = caseSearchConfig;
        this.objectMapper = objectMapper;
    }

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String API_PATH = "/v1/chat/completions";

    /**
     * 发送对话请求
     */
    public ChatResponse chat(ChatRequest request) {
        // 设置默认模型
        if (request.getModel() == null || request.getModel().isEmpty()) {
            request.setModel(xiaoliConfig.getModel());
        }

        // 构建请求
        String url = xiaoliConfig.getBaseUrl() + API_PATH;
        String jsonBody;
        try {
            jsonBody = objectMapper.writeValueAsString(request);
        } catch (Exception e) {
            throw new BusinessException("请求参数序列化失败");
        }

        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request httpRequest = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + xiaoliConfig.getKey())
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        // 发送请求
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(xiaoliConfig.getTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(xiaoliConfig.getTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(xiaoliConfig.getTimeout(), TimeUnit.MILLISECONDS)
                .build();

        try (Response response = client.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                log.error("小理AI调用失败: {} - {}", response.code(), response.message());
                throw new BusinessException("AI服务调用失败: " + response.message());
            }

            String responseBody = response.body() != null ? response.body().string() : "";
            return objectMapper.readValue(responseBody, ChatResponse.class);
        } catch (IOException e) {
            log.error("小理AI调用异常", e);
            throw new BusinessException("AI服务调用异常: " + e.getMessage());
        }
    }

    /**
     * 简单对话
     */
    public String chat(String message) {
        ChatRequest request = ChatRequest.builder()
                .messages(List.of(
                        ChatRequest.ChatMessage.builder()
                                .role("user")
                                .content(message)
                                .build()
                ))
                .build();

        ChatResponse response = chat(request);
        return response.getContent();
    }

    /**
     * 带上下文的对话
     */
    public String chat(String message, String systemPrompt) {
        var messages = List.of(
                ChatRequest.ChatMessage.builder()
                        .role("system")
                        .content(systemPrompt)
                        .build(),
                ChatRequest.ChatMessage.builder()
                        .role("user")
                        .content(message)
                        .build()
        );
        
        ChatRequest request = ChatRequest.builder()
                .messages(messages)
                .build();

        ChatResponse response = chat(request);
        return response.getContent();
    }

    /**
     * 法律咨询对话
     */
    public String legalConsult(String question) {
        String systemPrompt = "你是一位专业的法律顾问助手，专门为用户提供法律咨询服务。" +
                "请用专业、清晰、易懂的语言回答用户的法律问题。" +
                "回答应该包含以下内容：" +
                "1. 法律依据（相关法律法规条款）" +
                "2. 法律分析（针对问题的法律解读）" +
                "3. 建议措施（用户应该采取的行动）" +
                "4. 注意事项（可能的风险和注意事项）" +
                "5. 维权流程（如果用户询问如何维护自身权益，应提供详细的维权步骤和流程）" +
                "维权流程通常包括：" +
                "  - 证据收集阶段：整理和保全所有相关证据" +
                "  - 协商调解阶段：尝试与对方协商或申请调解" +
                "  - 投诉举报阶段：向相关部门投诉举报" +
                "  - 仲裁申请阶段：申请劳动仲裁或商事仲裁" +
                "  - 诉讼阶段：向人民法院提起诉讼" +
                "  - 执行阶段：申请强制执行" +
                "根据具体纠纷类型（劳动纠纷、合同纠纷、侵权纠纷、婚姻家庭纠纷等），" +
                "提供最适合的维权路径和时效提醒。" +
                "如果问题涉及具体案件，建议用户咨询专业律师。" +
                "对于你不确定的信息，请明确告知用户并建议咨询专业人士。";

        return chat(question, systemPrompt);
    }

    /**
     * 法律咨询对话（含相似案例检索）
     */
    public ConsultResult legalConsultWithSimilarCases(String question) {
        // 调用AI获取回答
        String answer = legalConsult(question);

        // 检索相似案例
        List<SimilarCase> similarCases = caseSearchClient.searchSimilarCases(
                question,
                caseSearchConfig.getDefaultLimit()
        );

        log.info("法律咨询[{}]检索到{}个相似案例", question, similarCases.size());

        return new ConsultResult(answer, similarCases);
    }

    /**
     * 咨询结果（含相似案例）
     */
    public static class ConsultResult {
        private String answer;
        private List<SimilarCase> similarCases;

        public ConsultResult(String answer, List<SimilarCase> similarCases) {
            this.answer = answer;
            this.similarCases = similarCases;
        }

        // Getters
        public String getAnswer() { return answer; }
        public List<SimilarCase> getSimilarCases() { return similarCases; }

        // Setters
        public void setAnswer(String answer) { this.answer = answer; }
        public void setSimilarCases(List<SimilarCase> similarCases) { this.similarCases = similarCases; }
    }
}
