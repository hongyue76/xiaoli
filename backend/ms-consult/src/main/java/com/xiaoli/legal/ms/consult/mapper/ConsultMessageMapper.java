package com.xiaoli.legal.ms.consult.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaoli.legal.ms.consult.model.entity.ConsultMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 咨询消息Mapper
 */
@Mapper
public interface ConsultMessageMapper extends BaseMapper<ConsultMessage> {
}
