package com.xiaoli.legal.common.ai.service;

import com.xiaoli.legal.common.ai.dto.TtsRequest;

/**
 * 语音合成服务
 */
public interface TtsService {

    /**
     * 文本转语音
     *
     * @param request 请求
     * @return 音频数据(Base64编码)
     */
    String speech(TtsRequest request);

    /**
     * 简单文本转语音
     *
     * @param text 文本
     * @return 音频数据(Base64编码)
     */
    String speech(String text);

    /**
     * 文本转语音(流式)
     *
     * @param text     文本
     * @param consumer 流式回调
     */
    void speechStream(String text, Consumer consumer);

    /**
     * 流式消费者接口
     */
    interface Consumer {
        /**
         * 接收音频数据
         *
         * @param audioData 音频数据(Base64)
         * @param done      是否结束
         */
        void accept(String audioData, boolean done);
    }
}
