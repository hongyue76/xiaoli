package com.xiaoli.legal.speech.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaoli.legal.speech.model.dto.SpeechRequest;
import com.xiaoli.legal.speech.model.dto.SpeechResponse;
import com.xiaoli.legal.speech.model.entity.SpeechSession;
import com.xiaoli.legal.speech.service.AsrService;
import com.xiaoli.legal.speech.service.TtsService;
import com.xiaoli.legal.speech.service.VoiceChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 语音对话服务实现
 * 整合ASR + 小理AI + TTS
 */
@Service
public class VoiceChatServiceImpl implements VoiceChatService {
    
    private static final Logger log = LoggerFactory.getLogger(VoiceChatServiceImpl.class);
    
    private final AsrService asrService;
    private final TtsService ttsService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${xiaoli.api.base-url}")
    private String xiaoliApiUrl;
    
    @Value("${xiaoli.api.api-key}")
    private String xiaoliApiKey;
    
    @Value("${xiaoli.api.model}")
    private String xiaoliModel;
    
    // 会话存储(生产环境应使用数据库)
    private final Map<String, SpeechSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, List<SpeechResponse.DialogueItem>> dialogueHistory = new ConcurrentHashMap<>();
    
    public VoiceChatServiceImpl(AsrService asrService, TtsService ttsService, 
                               RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.asrService = asrService;
        this.ttsService = ttsService;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public SpeechResponse processVoiceChat(SpeechRequest request) {
        SpeechResponse response = new SpeechResponse();
        
        try {
            // 1. 语音识别(ASR)
            SpeechResponse asrResult = asrService.recognize(
                request.getAudioData(),
                request.getFormat(),
                request.getSampleRate()
            );
            
            if (asrResult.getError() != null) {
                response.setError(asrResult.getError());
                return response;
            }
            
            String recognizedText = asrResult.getRecognizedText();
            response.setRecognizedText(recognizedText);
            response.setConfidence(asrResult.getConfidence());
            
            // 2. 调用小理AI获取回复
            String aiResponseText = callXiaoliAI(recognizedText, request.getSessionId());
            response.setAiResponseText(aiResponseText);
            
            // 3. 语音合成(TTS)
            SpeechResponse ttsResult = ttsService.synthesize(aiResponseText);
            response.setAudioData(ttsResult.getAudioData());
            response.setAudioDuration(ttsResult.getAudioDuration());
            
            // 4. 保存对话历史
            saveDialogue(request.getSessionId(), recognizedText, aiResponseText);
            
            response.setSessionId(request.getSessionId());
            response.setEndOfResponse(true);
            response.setSuccess(true);
            
        } catch (Exception e) {
            log.error("语音对话处理失败: {}", e.getMessage());
            response.setError("处理失败: " + e.getMessage());
            response.setSuccess(false);
        }
        
        return response;
    }
    
    @Override
    public String createSession(String sessionType, Long userId) {
        String sessionId = UUID.randomUUID().toString();
        
        SpeechSession session = new SpeechSession();
        session.setSessionId(sessionId);
        session.setUserId(userId);
        session.setSessionType(sessionType);
        session.setStartTime(LocalDateTime.now());
        session.setStatus("ACTIVE");
        session.setMessageCount(0);
        session.setAsrErrorCount(0);
        
        sessions.put(sessionId, session);
        dialogueHistory.put(sessionId, new ArrayList<>());
        
        log.info("创建语音会话: {}", sessionId);
        return sessionId;
    }
    
    @Override
    public void endSession(String sessionId) {
        SpeechSession session = sessions.get(sessionId);
        if (session != null) {
            session.setStatus("ENDED");
            session.setEndTime(LocalDateTime.now());
            session.setDuration((int) java.time.Duration.between(
                session.getStartTime(), session.getEndTime()
            ).getSeconds());
            
            // 统计消息数量
            List<SpeechResponse.DialogueItem> history = dialogueHistory.get(sessionId);
            if (history != null) {
                session.setMessageCount(history.size() / 2);
            }
            
            log.info("结束语音会话: {}, 消息数: {}, 时长: {}秒", 
                sessionId, session.getMessageCount(), session.getDuration());
        }
    }
    
    @Override
    public SpeechResponse getDialogueHistory(String sessionId) {
        SpeechResponse response = new SpeechResponse();
        response.setSessionId(sessionId);
        
        List<SpeechResponse.DialogueItem> history = dialogueHistory.get(sessionId);
        if (history != null) {
            response.setDialogueHistory(history);
        }
        
        return response;
    }
    
    /**
     * 调用小理AI获取回复
     */
    private String callXiaoliAI(String userMessage, String sessionId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(xiaoliApiKey);
            
            // 构建提示词(法律场景)
            String systemPrompt = buildLegalSystemPrompt();
            
            Map<String, Object> body = new HashMap<>();
            body.put("model", xiaoliModel);
            body.put("messages", Arrays.asList(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userMessage)
            ));
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                xiaoliApiUrl + "/v1/chat/completions",
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            if (response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
            
        } catch (Exception e) {
            log.error("调用小理AI失败: {}", e.getMessage());
        }
        
        // 返回默认回复
        return getDefaultResponse(userMessage);
    }
    
    /**
     * 构建法律场景系统提示词
     */
    private String buildLegalSystemPrompt() {
        return "你是一位专业的法律AI助手，专门为用户提供法律咨询服务。" +
               "请用专业、严谨但易懂的语言回答用户的问题。" +
               "对于具体的法律建议，请提示用户咨询专业律师。" +
               "回答应该简洁明了，控制在200字以内。";
    }
    
    /**
     * 获取默认回复
     */
    private String getDefaultResponse(String userMessage) {
        // 根据关键词返回默认回复
        if (userMessage.contains("劳动合同") || userMessage.contains("辞退")) {
            return "关于劳动合同纠纷，建议您先收集相关证据，如工资条、考勤记录、工作证等。" +
                   "如果单位违法解除劳动合同，可以申请劳动仲裁维护合法权益。";
        } else if (userMessage.contains("合同") || userMessage.contains("违约")) {
            return "关于合同违约问题，需要先查看合同的具体条款。" +
                   "建议您保留好相关证据，必要时可以委托律师发函协商或提起诉讼。";
        } else if (userMessage.contains("离婚") || userMessage.contains("财产")) {
            return "关于婚姻财产问题，夫妻共同财产原则上平均分割。" +
                   "建议您先梳理清楚夫妻共同财产的范围，必要时可以咨询专业律师。";
        }
        
        return "感谢您的咨询。您描述的情况我已了解。" +
               "根据法律规定，建议您进一步收集相关证据，" +
               "如有需要可以委托专业律师提供更精准的法律服务。";
    }
    
    /**
     * 保存对话历史
     */
    private void saveDialogue(String sessionId, String userText, String aiText) {
        List<SpeechResponse.DialogueItem> history = dialogueHistory.get(sessionId);
        if (history == null) {
            history = new ArrayList<>();
            dialogueHistory.put(sessionId, history);
        }
        
        long timestamp = System.currentTimeMillis();
        
        // 添加用户消息
        SpeechResponse.DialogueItem userItem = new SpeechResponse.DialogueItem();
        userItem.setRole("USER");
        userItem.setText(userText);
        userItem.setTimestamp(timestamp);
        history.add(userItem);
        
        // 添加AI回复
        SpeechResponse.DialogueItem aiItem = new SpeechResponse.DialogueItem();
        aiItem.setRole("AI");
        aiItem.setText(aiText);
        aiItem.setTimestamp(timestamp + 1);
        history.add(aiItem);
        
        // 限制历史长度
        if (history.size() > 50) {
            history.subList(0, history.size() - 50).clear();
        }
    }

    /**
     * 异步语音合成TTS（用于后台合成，不阻塞主流程）
     */
    @Async("asyncExecutor")
    public CompletableFuture<SpeechResponse> synthesizeAsync(String text) {
        return CompletableFuture.completedFuture(ttsService.synthesize(text));
    }
}
