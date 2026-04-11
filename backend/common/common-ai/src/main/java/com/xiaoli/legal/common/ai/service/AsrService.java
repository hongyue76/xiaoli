package com.xiaoli.legal.common.ai.service;

import com.xiaoli.legal.common.ai.dto.AsrRequest;
import com.xiaoli.legal.common.ai.dto.AsrResponse;

/**
 * 语音识别服务
 */
public interface AsrService {

    /**
     * 语音转文字
     *
     * @param audioData 音频数据(Base64编码)
     * @param request   请求参数
     * @return 识别结果
     */
    AsrResponse recognize(String audioData, AsrRequest request);

    /**
     * 简短语语音转文字
     *
     * @param audioData 音频数据(Base64编码)
     * @return 识别文字
     */
    String recognize(String audioData);

    /**
     * 流体语音识别
     *
     * @param audioData 音频数据(Base64编码)
     * @param consumer  流体回调
     */
    void recognizeStream(String audioData, Consumer consumer);

    /**
     * 流体消费者
     */
    interface Consumer {
        /**
         * 接收识别结果
         *
         * @param result 识别结果
         * @param done   是否结束
         */
        void accept(AsrResponse result, boolean done);
    }
}
