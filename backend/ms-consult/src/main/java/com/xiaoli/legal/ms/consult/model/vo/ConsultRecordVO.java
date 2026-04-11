package com.xiaoli.legal.ms.consult.model.vo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 咨询记录VO
 */
public class ConsultRecordVO {

    /**
     * 会话ID
     */
    private Long conversationId;

    /**
     * 会话标题
     */
    private String title;

    /**
     * 会话类型
     */
    private String conversationType;

    /**
     * 会话状态
     */
    private String status;

    /**
     * 消息列表
     */
    private List<MessageVO> messages;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    // Getters and Setters
    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getConversationType() { return conversationType; }
    public void setConversationType(String conversationType) { this.conversationType = conversationType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<MessageVO> getMessages() { return messages; }
    public void setMessages(List<MessageVO> messages) { this.messages = messages; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    /**
     * 消息VO
     */
    public static class MessageVO {
        /**
         * 消息ID
         */
        private Long id;

        /**
         * 消息角色
         */
        private String role;

        /**
         * 消息内容
         */
        private String content;

        /**
         * 消息类型
         */
        private String messageType;

        /**
         * 创建时间
         */
        private LocalDateTime createTime;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public String getMessageType() { return messageType; }
        public void setMessageType(String messageType) { this.messageType = messageType; }

        public LocalDateTime getCreateTime() { return createTime; }
        public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    }
}
