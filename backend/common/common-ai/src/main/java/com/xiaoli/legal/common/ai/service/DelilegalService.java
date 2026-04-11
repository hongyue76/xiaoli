package com.xiaoli.legal.common.ai.service;

import java.util.List;

/**
 * 得理法搜案例检索服务
 */
public interface DelilegalService {

    /**
     * 案例检索
     * @param keywords 关键词数组
     * @param longText 长文本语义检索
     * @param courtLevelArr 法院层级筛选
     * @param judgementTypeArr 文书类型筛选
     * @return 案例列表JSON
     */
    String searchCases(List<String> keywords, String longText, 
                       List<String> courtLevelArr, List<String> judgementTypeArr);

    /**
     * 法规检索
     * @param keywords 查询关键词
     * @param fieldName 检索方式：title-关键词检索, semantic-语义检索
     * @return 法规列表JSON
     */
    String searchLaws(String keywords, String fieldName);
}