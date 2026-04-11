package com.xiaoli.legal.ms.legalcase.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaoli.legal.common.core.domain.PageResult;
import com.xiaoli.legal.common.core.exception.BusinessException;
import com.xiaoli.legal.common.core.domain.ResultCode;
import com.xiaoli.legal.ms.legalcase.mapper.LegalCaseMapper;
import com.xiaoli.legal.ms.legalcase.model.dto.CaseSearchRequest;
import com.xiaoli.legal.ms.legalcase.model.entity.LegalCase;
import com.xiaoli.legal.ms.legalcase.model.vo.CaseSearchResultVO;
import com.xiaoli.legal.ms.legalcase.service.CaseSearchService;
import com.xiaoli.legal.common.ai.service.XiaoliChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 案例检索服务实现
 */
@Service
public class CaseSearchServiceImpl implements CaseSearchService {

    private static final Logger log = LoggerFactory.getLogger(CaseSearchServiceImpl.class);

    private final LegalCaseMapper caseMapper;
    private final XiaoliChatService xiaoliChatService;

    public CaseSearchServiceImpl(LegalCaseMapper caseMapper, XiaoliChatService xiaoliChatService) {
        this.caseMapper = caseMapper;
        this.xiaoliChatService = xiaoliChatService;
    }

    @Override
    public PageResult<CaseSearchResultVO> searchCases(CaseSearchRequest request) {
        // 如果启用语义检索
        if (Boolean.TRUE.equals(request.getSemantic())) {
            return semanticSearch(request);
        }

        // 普通关键词检索
        return keywordSearch(request);
    }

    /**
     * 关键词检索
     */
    private PageResult<CaseSearchResultVO> keywordSearch(CaseSearchRequest request) {
        Page<LegalCase> page = new Page<>(request.getCurrent(), request.getSize());

        LambdaQueryWrapper<LegalCase> wrapper = new LambdaQueryWrapper<LegalCase>()
                .like(StrUtil.isNotBlank(request.getKeyword()), LegalCase::getTitle, request.getKeyword())
                .or()
                .like(StrUtil.isNotBlank(request.getKeyword()), LegalCase::getSummary, request.getKeyword())
                .eq(StrUtil.isNotBlank(request.getCaseType()), LegalCase::getCaseType, request.getCaseType())
                .eq(StrUtil.isNotBlank(request.getCourt()), LegalCase::getCourt, request.getCourt())
                .eq(StrUtil.isNotBlank(request.getCause()), LegalCase::getCause, request.getCause())
                .eq(StrUtil.isNotBlank(request.getCaseStatus()), LegalCase::getCaseStatus, request.getCaseStatus())
                .eq(request.getYear() != null, LegalCase::getJudgmentDate, request.getYear())
                .like(StrUtil.isNotBlank(request.getJudge()), LegalCase::getJudge, request.getJudge());

        // 排序
        if ("DATE".equals(request.getSortBy())) {
            wrapper.orderByDesc(LegalCase::getJudgmentDate);
        } else if ("VIEW".equals(request.getSortBy())) {
            wrapper.orderByDesc(LegalCase::getViewCount);
        } else {
            wrapper.orderByDesc(LegalCase::getCreateTime);
        }

        Page<LegalCase> result = caseMapper.selectPage(page, wrapper);

        List<CaseSearchResultVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(result.getCurrent(), result.getSize(), result.getTotal(), voList);
    }

    @Override
    public PageResult<CaseSearchResultVO> searchByJudge(String judgeName, String caseType, Long current, Long size) {
        Page<LegalCase> page = new Page<>(current, size);

        LambdaQueryWrapper<LegalCase> wrapper = new LambdaQueryWrapper<LegalCase>()
                .like(StrUtil.isNotBlank(judgeName), LegalCase::getJudge, judgeName)
                .eq(StrUtil.isNotBlank(caseType), LegalCase::getCaseType, caseType)
                .orderByDesc(LegalCase::getJudgmentDate);

        Page<LegalCase> result = caseMapper.selectPage(page, wrapper);

        List<CaseSearchResultVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(result.getCurrent(), result.getSize(), result.getTotal(), voList);
    }

    @Override
    public PageResult<CaseSearchResultVO> semanticSearch(CaseSearchRequest request) {
        try {
            // 获取文本向量
            String searchText = buildSearchText(request);
            float[] embedding = getEmbedding(searchText);

            // TODO: 调用Milvus进行向量检索
            // 这里暂时使用关键词检索作为回退
            log.info("执行语义检索: {}", request.getKeyword());
            return keywordSearch(request);

        } catch (Exception e) {
            log.error("语义检索失败，使用关键词检索", e);
            return keywordSearch(request);
        }
    }

    @Override
    public CaseSearchResultVO getCaseDetail(Long caseId) {
        LegalCase legalCase = caseMapper.selectById(caseId);
        if (legalCase == null) {
            throw new BusinessException(ResultCode.CASE_NOT_EXIST);
        }

        // 增加浏览量
        legalCase.setViewCount(legalCase.getViewCount() + 1);
        caseMapper.updateById(legalCase);

        return convertToVO(legalCase);
    }

    @Override
    public List<CaseSearchResultVO> getSimilarCases(Long caseId, Long limit) {
        LegalCase legalCase = caseMapper.selectById(caseId);
        if (legalCase == null) {
            throw new BusinessException(ResultCode.CASE_NOT_EXIST);
        }

        // 基于案由和案件类型查找相似案例
        CaseSearchRequest request = new CaseSearchRequest();
        request.setCaseType(legalCase.getCaseType());
        request.setCause(legalCase.getCause());
        request.setCurrent(1L);
        request.setSize(limit);
        request.setSemantic(false);

        PageResult<CaseSearchResultVO> result = keywordSearch(request);
        return result.getRecords().stream()
                .filter(c -> !c.getId().equals(caseId))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<CaseSearchResultVO> getHotCases(String caseType, Long limit) {
        LambdaQueryWrapper<LegalCase> wrapper = new LambdaQueryWrapper<LegalCase>()
                .eq(StrUtil.isNotBlank(caseType), LegalCase::getCaseType, caseType)
                .orderByDesc(LegalCase::getViewCount)
                .last("LIMIT " + limit);

        List<LegalCase> cases = caseMapper.selectList(wrapper);
        return cases.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public void rebuildIndex() {
        // TODO: 实现索引重建，将所有案例添加到向量数据库
        log.info("开始重建案例索引");
    }

    @Override
    public void addCaseToIndex(Long caseId) {
        // TODO: 实现单个案例索引添加
        log.info("添加案例到索引: {}", caseId);
    }

    /**
     * 获取文本向量
     */
    private float[] getEmbedding(String text) {
        // TODO: 调用小理AI Embedding API
        // 暂时返回随机向量
        return new float[1536];
    }

    /**
     * 构建检索文本
     */
    private String buildSearchText(CaseSearchRequest request) {
        StringBuilder sb = new StringBuilder();
        if (StrUtil.isNotBlank(request.getKeyword())) {
            sb.append(request.getKeyword()).append(" ");
        }
        if (StrUtil.isNotBlank(request.getCaseType())) {
            sb.append(request.getCaseType()).append(" ");
        }
        if (StrUtil.isNotBlank(request.getCause())) {
            sb.append(request.getCause()).append(" ");
        }
        return sb.toString();
    }

    /**
     * 转换实体为VO
     */
    private CaseSearchResultVO convertToVO(LegalCase legalCase) {
        CaseSearchResultVO vo = new CaseSearchResultVO();
        vo.setId(legalCase.getId());
        vo.setTitle(legalCase.getTitle());
        vo.setCaseNo(legalCase.getCaseNo());
        vo.setCaseType(legalCase.getCaseType());
        vo.setCause(legalCase.getCause());
        vo.setCourt(legalCase.getCourt());
        vo.setJudge(legalCase.getJudge());
        vo.setJudgmentDate(legalCase.getJudgmentDate());
        vo.setCaseStatus(legalCase.getCaseStatus());
        vo.setSummary(legalCase.getSummary());
        vo.setDisputeFocus(legalCase.getDisputeFocus());
        vo.setRulingIdea(legalCase.getRulingIdea());
        vo.setJudgmentResult(legalCase.getJudgmentResult());
        vo.setLegalBasis(legalCase.getLegalBasis());
        vo.setViewCount(legalCase.getViewCount());
        vo.setSource(legalCase.getSource());

        // 解析标签
        if (StrUtil.isNotBlank(legalCase.getTags())) {
            vo.setTags(JSON.parseArray(legalCase.getTags()).toJavaList(String.class));
        }

        return vo;
    }
}
