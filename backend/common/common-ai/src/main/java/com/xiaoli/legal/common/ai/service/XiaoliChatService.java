package com.xiaoli.legal.common.ai.service;

import com.xiaoli.legal.common.ai.dto.ChatRequest;
import com.xiaoli.legal.common.ai.dto.ChatResponse;

import java.util.List;

/**
 * 小理AI对话服务
 */
public interface XiaoliChatService {

    /**
     * 同步对话
     *
     * @param request 请求
     * @return 响应
     */
    ChatResponse chat(ChatRequest request);

    /**
     * 简单对话
     *
     * @param message 用户消息
     * @return AI回复
     */
    String chat(String message);

    /**
     * 带系统提示词的对话
     *
     * @param systemPrompt 系统提示词
     * @param message      用户消息
     * @return AI回复
     */
    String chat(String systemPrompt, String message);

    /**
     * 多轮对话
     *
     * @param messages 历史消息+当前消息
     * @return AI回复
     */
    String chat(List<ChatRequest.ChatMessage> messages);

    /**
     * 流体对话
     *
     * @param request    请求
     * @param consumer   流体回调
     */
    void chatStream(ChatRequest request, StreamConsumer consumer);

    /**
     * 流体消费者接口
     */
    interface StreamConsumer {
        /**
         * 接收内容片段
         *
         * @param content 内容片段
         * @param done    是否结束
         */
        void accept(String content, boolean done);
    }
}
