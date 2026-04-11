package com.xiaoli.legal.common.ai.service;

import com.xiaoli.legal.common.ai.dto.EmbeddingRequest;
import com.xiaoli.legal.common.ai.dto.EmbeddingResponse;

import java.util.List;

/**
 * 向量嵌入服务
 */
public interface EmbeddingService {

    /**
     * 单条文本向量生成
     *
     * @param text 文本
     * @return 向量
     */
    float[] embed(String text);

    /**
     * 批量文本向量生成
     *
     * @param texts 文本列表
     * @return 向量列表
     */
    List<float[]> embed(List<String> texts);

    /**
     * 获取向量维度
     *
     * @return 维度
     */
    int getDimension();
}
