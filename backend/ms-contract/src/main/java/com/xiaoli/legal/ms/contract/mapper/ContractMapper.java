package com.xiaoli.legal.ms.contract.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaoli.legal.ms.contract.model.entity.Contract;
import org.apache.ibatis.annotations.Mapper;

/**
 * 合同Mapper
 */
@Mapper
public interface ContractMapper extends BaseMapper<Contract> {
}
