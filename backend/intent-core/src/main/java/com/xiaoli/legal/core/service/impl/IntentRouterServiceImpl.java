package com.xiaoli.legal.core.service.impl;

import com.xiaoli.legal.core.IntentType;
import com.xiaoli.legal.core.AIEngineType;
import com.xiaoli.legal.core.engine.LLMEngineProcessor;
import com.xiaoli.legal.core.engine.RuleEngineProcessor;
import com.xiaoli.legal.core.model.EngineSelectionResult;
import com.xiaoli.legal.core.model.IntentResult;
import com.xiaoli.legal.core.model.UserQuery;
import com.xiaoli.legal.core.service.EngineRouterService;
import com.xiaoli.legal.core.service.IntentRecognitionService;
import com.xiaoli.legal.core.service.IntentRouterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 统一意图路由服务实现
 */
@Service
public class IntentRouterServiceImpl implements IntentRouterService {

    private static final Logger log = LoggerFactory.getLogger(IntentRouterServiceImpl.class);

    @Autowired
    private IntentRecognitionService intentRecognitionService;

    @Autowired
    private EngineRouterService engineRouterService;

    @Autowired
    private RuleEngineProcessor ruleEngineProcessor;

    @Autowired
    private LLMEngineProcessor llmEngineProcessor;

    @Override
    public QueryResponse processQuery(UserQuery query) {
        long startTime = System.currentTimeMillis();

        log.info("开始处理用户查询: {}", query.getQueryText());

        try {
            // 1. 意图识别
            IntentResult intentResult = recognizeOnly(query);
            log.info("意图识别结果: {}, 置信度: {}",
                intentResult.getIntent().getDisplayName(), intentResult.getConfidence());

            // 2. 引擎路由
            EngineSelectionResult engineResult = routeOnly(query, intentResult);
            log.info("引擎路由结果: {}, 原因: {}",
                engineResult.getEngineType().getDisplayName(), engineResult.getReason());

            // 3. 选择引擎并处理
            String response;
            if (engineResult.getEngineType() == AIEngineType.RULE_BASED) {
                log.info("使用规则引擎处理");
                response = ruleEngineProcessor.process(query, intentResult);
            } else {
                log.info("使用LLM引擎处理");
                response = llmEngineProcessor.process(query, intentResult);
            }

            long responseTime = System.currentTimeMillis() - startTime;
            log.info("查询处理完成, 耗时: {}ms", responseTime);

            // 4. 返回结果
            return QueryResponse.builder()
                .queryText(query.getQueryText())
                .intentResult(intentResult)
                .engineResult(engineResult)
                .response(response)
                .responseTime(responseTime)
                .success(true)
                .build();

        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            log.error("查询处理失败: {}", e.getMessage(), e);

            return QueryResponse.builder()
                .queryText(query.getQueryText())
                .intentResult(IntentResult.builder()
                    .intent(IntentType.UNKNOWN)
                    .confidence(0.0)
                    .explanation("处理失败")
                    .build())
                .engineResult(EngineSelectionResult.builder()
                    .engineType(AIEngineType.RULE_BASED)
                    .reason("降级到规则引擎")
                    .build())
                .response("抱歉，处理您的请求时发生了错误。请稍后重试或联系管理员。")
                .responseTime(responseTime)
                .success(false)
                .errorMessage(e.getMessage())
                .build();
        }
    }

    @Override
    public IntentResult recognizeOnly(UserQuery query) {
        return intentRecognitionService.recognizeIntent(query);
    }

    @Override
    public EngineSelectionResult routeOnly(UserQuery query, IntentResult intentResult) {
        return engineRouterService.route(query, intentResult);
    }
}
