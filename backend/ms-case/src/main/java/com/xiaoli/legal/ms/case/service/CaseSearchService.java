package com.xiaoli.legal.ms.legalcase.service;

import com.xiaoli.legal.common.core.domain.PageResult;
import com.xiaoli.legal.ms.legalcase.model.dto.CaseSearchRequest;
import com.xiaoli.legal.ms.legalcase.model.vo.CaseSearchResultVO;

import java.util.List;

/**
 * 案例检索服务接口
 */
public interface CaseSearchService {

    /**
     * 案例检索
     */
    PageResult<CaseSearchResultVO> searchCases(CaseSearchRequest request);

    /**
     * 语义检索
     */
    PageResult<CaseSearchResultVO> semanticSearch(CaseSearchRequest request);

    /**
     * 获取案例详情
     */
    CaseSearchResultVO getCaseDetail(Long caseId);

    /**
     * 获取相似案例
     */
    List<CaseSearchResultVO> getSimilarCases(Long caseId, Long limit);

    /**
     * 获取热门案例
     */
    List<CaseSearchResultVO> getHotCases(String caseType, Long limit);

    /**
     * 按法官搜索案例
     */
    PageResult<CaseSearchResultVO> searchByJudge(String judgeName, String caseType, Long current, Long size);

    /**
     * 案例索引重建
     */
    void rebuildIndex();

    /**
     * 添加案例到索引
     */
    void addCaseToIndex(Long caseId);
}
