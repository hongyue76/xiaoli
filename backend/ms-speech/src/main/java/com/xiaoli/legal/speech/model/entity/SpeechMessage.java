package com.xiaoli.legal.speech.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 语音消息实体
 */
@Entity
@Table(name = "speech_message")
public class SpeechMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 会话ID */
    @Column(name = "session_id")
    private Long sessionId;
    
    /** 消息方向: USER(用户)/AI(AI助手) */
    @Column(name = "direction", length = 10)
    private String direction;
    
    /** 语音识别文本(用户) */
    @Column(name = "asr_text", columnDefinition = "TEXT")
    private String asrText;
    
    /** 合成文本(AI回复) */
    @Column(name = "tts_text", columnDefinition = "TEXT")
    private String ttsText;
    
    /** 原始音频URL(用户) */
    @Column(name = "audio_url", length = 500)
    private String audioUrl;
    
    /** 合成音频URL(AI回复) */
    @Column(name = "synthesized_audio_url", length = 500)
    private String synthesizedAudioUrl;
    
    /** 语音时长(秒) */
    @Column(name = "audio_duration")
    private Double audioDuration;
    
    /** 识别置信度 */
    @Column(name = "confidence")
    private Double confidence;
    
    /** 是否成功 */
    @Column(name = "success")
    private Boolean success;
    
    /** 错误信息 */
    @Column(name = "error_message", length = 500)
    private String errorMessage;
    
    /** 创建时间 */
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
    
    public String getAsrText() { return asrText; }
    public void setAsrText(String asrText) { this.asrText = asrText; }
    
    public String getTtsText() { return ttsText; }
    public void setTtsText(String ttsText) { this.ttsText = ttsText; }
    
    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    
    public String getSynthesizedAudioUrl() { return synthesizedAudioUrl; }
    public void setSynthesizedAudioUrl(String synthesizedAudioUrl) { this.synthesizedAudioUrl = synthesizedAudioUrl; }
    
    public Double getAudioDuration() { return audioDuration; }
    public void setAudioDuration(Double audioDuration) { this.audioDuration = audioDuration; }
    
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    
    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
