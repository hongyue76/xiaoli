package com.xiaoli.legal.ms.consult.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 咨询消息
 */
@TableName("consult_messages")
public class ConsultMessage {

    /**
     * 消息ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID
     */
    private Long conversationId;

    /**
     * 消息角色: USER-用户, ASSISTANT-AI助手, SYSTEM-系统
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型: TEXT-文本, VOICE-语音, IMAGE-图片
     */
    private String messageType;

    /**
     * 引用知识ID (如果有)
     */
    private String referenceKnowledge;

    /**
     * 引用案例ID (如果有)
     */
    private String referenceCase;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public String getReferenceKnowledge() { return referenceKnowledge; }
    public void setReferenceKnowledge(String referenceKnowledge) { this.referenceKnowledge = referenceKnowledge; }

    public String getReferenceCase() { return referenceCase; }
    public void setReferenceCase(String referenceCase) { this.referenceCase = referenceCase; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
