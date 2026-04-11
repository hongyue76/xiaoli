package com.xiaoli.legal.common.ai.service;

import java.util.List;
import java.util.Map;

/**
 * 腾讯元器智能体对话服务
 */
public interface YuanqiChatService {

    /**
     * 发送对话消息
     * @param message 用户消息
     * @return 智能体回复
     */
    String chat(String message);

    /**
     * 发送对话消息（带system prompt）
     * @param systemPrompt 系统提示
     * @param message 用户消息
     * @return 智能体回复
     */
    String chat(String systemPrompt, String message);

    /**
     * 流式对话
     * @param message 用户消息
     * @param consumer 流式消费回调
     */
    void chatStream(String message, StreamConsumer consumer);

    /**
     * 流式消费回调接口
     */
    interface StreamConsumer {
        /**
         * 接收内容
         * @param content 内容片段
         * @param isFinal 是否结束
         */
        void accept(String content, boolean isFinal);
    }
}
