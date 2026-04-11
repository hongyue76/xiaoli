package com.xiaoli.legal.speech.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 语音对话会话实体
 */
@Entity
@Table(name = "speech_session")
public class SpeechSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 会话ID */
    @Column(name = "session_id", length = 64, unique = true)
    private String sessionId;
    
    /** 用户ID */
    @Column(name = "user_id")
    private Long userId;
    
    /** 会话类型: CONSULT(咨询)/CASE_ANALYSIS(案件分析)/CONTRACT_REVIEW(合同审查) */
    @Column(name = "session_type", length = 30)
    private String sessionType;
    
    /** 开始时间 */
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    /** 结束时间 */
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    /** 持续时长(秒) */
    @Column(name = "duration")
    private Integer duration;
    
    /** 消息数量 */
    @Column(name = "message_count")
    private Integer messageCount;
    
    /** 识别错误数 */
    @Column(name = "asr_error_count")
    private Integer asrErrorCount;
    
    /** 会话状态: ACTIVE/ENDED */
    @Column(name = "status", length = 20)
    private String status;
    
    /** 创建时间 */
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        if (this.startTime == null) {
            this.startTime = LocalDateTime.now();
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    
    public Integer getMessageCount() { return messageCount; }
    public void setMessageCount(Integer messageCount) { this.messageCount = messageCount; }
    
    public Integer getAsrErrorCount() { return asrErrorCount; }
    public void setAsrErrorCount(Integer asrErrorCount) { this.asrErrorCount = asrErrorCount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
