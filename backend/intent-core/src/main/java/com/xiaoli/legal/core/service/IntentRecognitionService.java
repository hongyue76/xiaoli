package com.xiaoli.legal.core.service;

import com.xiaoli.legal.core.IntentType;
import com.xiaoli.legal.core.model.IntentResult;
import com.xiaoli.legal.core.model.UserQuery;

/**
 * 意图识别服务接口
 */
public interface IntentRecognitionService {

    /**
     * 识别用户查询意图
     *
     * @param query 用户查询
     * @return 意图识别结果
     */
    IntentResult recognizeIntent(UserQuery query);

    /**
     * 获取意图置信度阈值
     *
     * @param intent 意图类型
     * @return 阈值
     */
    Double getConfidenceThreshold(IntentType intent);
}
