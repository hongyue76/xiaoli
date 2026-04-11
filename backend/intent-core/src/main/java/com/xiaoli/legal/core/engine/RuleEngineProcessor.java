package com.xiaoli.legal.core.engine;

import com.xiaoli.legal.core.IntentType;
import com.xiaoli.legal.core.model.IntentResult;
import com.xiaoli.legal.core.model.UserQuery;

/**
 * 规则引擎处理器接口
 * 用于处理基于规则的快速响应
 */
public interface RuleEngineProcessor {

    /**
     * 处理用户查询
     *
     * @param query 用户查询
     * @param intentResult 意图识别结果
     * @return 处理结果
     */
    String process(UserQuery query, IntentResult intentResult);

    /**
     * 检查是否支持该意图
     *
     * @param intent 意图类型
     * @return 是否支持
     */
    boolean supports(IntentType intent);
}
