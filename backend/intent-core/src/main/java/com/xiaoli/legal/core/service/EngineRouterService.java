package com.xiaoli.legal.core.service;

import com.xiaoli.legal.core.AIEngineType;
import com.xiaoli.legal.core.IntentType;
import com.xiaoli.legal.core.model.EngineSelectionResult;
import com.xiaoli.legal.core.model.IntentResult;
import com.xiaoli.legal.core.model.UserQuery;

/**
 * 双引擎路由服务接口
 * 根据意图、复杂度、成本等因素智能选择AI引擎
 */
public interface EngineRouterService {

    /**
     * 路由用户查询到合适的引擎
     *
     * @param query 用户查询
     * @param intentResult 意图识别结果
     * @return 引擎选择结果
     */
    EngineSelectionResult route(UserQuery query, IntentResult intentResult);

    /**
     * 获取引擎选择的规则配置
     *
     * @param intent 意图类型
     * @param confidence 意图置信度
     * @return 引擎类型
     */
    AIEngineType getEngineByRule(IntentType intent, Double confidence);

    /**
     * 评估查询复杂度
     *
     * @param query 用户查询
     * @param intentResult 意图识别结果
     * @return 复杂度评分 0-1
     */
    Double assessComplexity(UserQuery query, IntentResult intentResult);

    /**
     * 计算引擎切换阈值
     *
     * @param intent 意图类型
     * @param complexity 复杂度
     * @return 切换阈值 0-1
     */
    Double calculateThreshold(IntentType intent, Double complexity);
}
