package com.xiaoli.legal.core.engine;

import com.xiaoli.legal.core.model.IntentResult;
import com.xiaoli.legal.core.model.UserQuery;

/**
 * LLM引擎处理器接口
 * 用于处理基于大语言模型的智能生成
 */
public interface LLMEngineProcessor {

    /**
     * 处理用户查询
     *
     * @param query 用户查询
     * @param intentResult 意图识别结果
     * @return 处理结果
     */
    String process(UserQuery query, IntentResult intentResult);

    /**
     * 流式处理（支持长文本生成）
     *
     * @param query 用户查询
     * @param intentResult 意图识别结果
     * @param callback 回调函数
     */
    void processStream(UserQuery query, IntentResult intentResult, StreamCallback callback);

    /**
     * 流式回调接口
     */
    interface StreamCallback {
        void onChunk(String chunk);
        void onComplete(String fullResponse);
        void onError(Throwable error);
    }
}
