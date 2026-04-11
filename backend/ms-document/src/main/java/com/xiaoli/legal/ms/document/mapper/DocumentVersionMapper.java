package com.xiaoli.legal.ms.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaoli.legal.ms.document.model.entity.DocumentVersion;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文档版本Mapper
 */
@Mapper
public interface DocumentVersionMapper extends BaseMapper<DocumentVersion> {
}
