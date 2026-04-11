package com.xiaoli.legal.ms.legalcase.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaoli.legal.ms.legalcase.model.entity.LegalCase;
import org.apache.ibatis.annotations.Mapper;

/**
 * 法律案例Mapper
 */
@Mapper
public interface LegalCaseMapper extends BaseMapper<LegalCase> {
}
