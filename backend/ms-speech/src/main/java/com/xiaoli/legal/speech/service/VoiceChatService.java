package com.xiaoli.legal.speech.service;

import com.xiaoli.legal.speech.model.dto.SpeechRequest;
import com.xiaoli.legal.speech.model.dto.SpeechResponse;

/**
 * 语音对话服务接口
 * 整合ASR + AI + TTS
 */
public interface VoiceChatService {
    
    /**
     * 处理语音对话
     * @param request 语音请求
     * @return 语音响应(含AI回复音频)
     */
    SpeechResponse processVoiceChat(SpeechRequest request);
    
    /**
     * 创建新会话
     * @param sessionType 会话类型
     * @param userId 用户ID
     * @return 会话ID
     */
    String createSession(String sessionType, Long userId);
    
    /**
     * 结束会话
     * @param sessionId 会话ID
     */
    void endSession(String sessionId);
    
    /**
     * 获取对话历史
     * @param sessionId 会话ID
     * @return 对话历史
     */
    SpeechResponse getDialogueHistory(String sessionId);
}
