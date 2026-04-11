package com.xiaoli.legal.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaoli.legal.analysis.model.entity.CaseAnalysis;
import org.apache.ibatis.annotations.Mapper;

/**
 * 案件分析Mapper
 */
@Mapper
public interface CaseAnalysisMapper extends BaseMapper<CaseAnalysis> {
}
