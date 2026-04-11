package com.xiaoli.legal.speech.model.dto;

/**
 * 语音请求DTO
 */
public class SpeechRequest {
    
    /** 会话ID */
    private String sessionId;
    
    /** 语音数据(base64编码) */
    private String audioData;
    
    /** 音频格式: wav/pcm/mp3 */
    private String format;
    
    /** 采样率: 8000/16000 */
    private Integer sampleRate;
    
    /** 语音编码: 1(pcm)/2(wav)/3(mp3) */
    private Integer codec;
    
    /** 是否结束会话 */
    private Boolean endOfSpeech;
    
    /** 会话类型 */
    private String sessionType;
    
    // Getters and Setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getAudioData() { return audioData; }
    public void setAudioData(String audioData) { this.audioData = audioData; }
    
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    
    public Integer getSampleRate() { return sampleRate; }
    public void setSampleRate(Integer sampleRate) { this.sampleRate = sampleRate; }
    
    public Integer getCodec() { return codec; }
    public void setCodec(Integer codec) { this.codec = codec; }
    
    public Boolean getEndOfSpeech() { return endOfSpeech; }
    public void setEndOfSpeech(Boolean endOfSpeech) { this.endOfSpeech = endOfSpeech; }
    
    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }
}
