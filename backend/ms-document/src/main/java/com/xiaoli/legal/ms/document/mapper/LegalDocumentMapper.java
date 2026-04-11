package com.xiaoli.legal.ms.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaoli.legal.ms.document.model.entity.LegalDocument;
import org.apache.ibatis.annotations.Mapper;

/**
 * 法律文书Mapper
 */
@Mapper
public interface LegalDocumentMapper extends BaseMapper<LegalDocument> {
}
