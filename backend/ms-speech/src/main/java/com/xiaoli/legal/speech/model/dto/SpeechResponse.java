package com.xiaoli.legal.speech.model.dto;

import java.util.List;

/**
 * 语音响应DTO
 */
public class SpeechResponse {
    
    /** 会话ID */
    private String sessionId;
    
    /** 消息ID */
    private Long messageId;
    
    /** 识别结果文本 */
    private String recognizedText;
    
    /** AI回复文本 */
    private String aiResponseText;
    
    /** 合成的语音URL */
    private String audioUrl;
    
    /** 语音数据(base64) */
    private String audioData;
    
    /** 音频时长(秒) */
    private Double audioDuration;
    
    /** 识别置信度 */
    private Double confidence;
    
    /** 是否结束 */
    private Boolean endOfResponse;
    
    /** 是否成功 */
    private Boolean success;
    
    /** 错误信息 */
    private String error;
    
    /** 完整对话历史 */
    private List<DialogueItem> dialogueHistory;
    
    // Getters and Setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }
    
    public String getRecognizedText() { return recognizedText; }
    public void setRecognizedText(String recognizedText) { this.recognizedText = recognizedText; }
    
    public String getAiResponseText() { return aiResponseText; }
    public void setAiResponseText(String aiResponseText) { this.aiResponseText = aiResponseText; }
    
    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    
    public String getAudioData() { return audioData; }
    public void setAudioData(String audioData) { this.audioData = audioData; }
    
    public Double getAudioDuration() { return audioDuration; }
    public void setAudioDuration(Double audioDuration) { this.audioDuration = audioDuration; }
    
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    
    public Boolean getEndOfResponse() { return endOfResponse; }
    public void setEndOfResponse(Boolean endOfResponse) { this.endOfResponse = endOfResponse; }
    
    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }
    
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    
    public List<DialogueItem> getDialogueHistory() { return dialogueHistory; }
    public void setDialogueHistory(List<DialogueItem> dialogueHistory) { this.dialogueHistory = dialogueHistory; }
    
    /**
     * 对话项
     */
    public static class DialogueItem {
        /** 角色: USER/AI */
        private String role;
        /** 文本 */
        private String text;
        /** 时间戳 */
        private Long timestamp;
        
        // Getters and Setters
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        
        public Long getTimestamp() { return timestamp; }
        public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    }
}
