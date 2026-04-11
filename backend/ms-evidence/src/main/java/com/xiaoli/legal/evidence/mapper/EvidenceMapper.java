package com.xiaoli.legal.evidence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaoli.legal.evidence.model.entity.Evidence;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 证据Mapper
 */
@Mapper
public interface EvidenceMapper extends BaseMapper<Evidence> {

    /**
     * 批量插入证据
     */
    @Insert("<script>" +
            "INSERT INTO evidence (name, evidence_type, source, purpose, content, file_path, file_type, file_size, " +
            "case_id, review_status, user_id, create_time, update_time) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.name}, #{item.evidenceType}, #{item.source}, #{item.purpose}, #{item.content}, " +
            "#{item.filePath}, #{item.fileType}, #{item.fileSize}, #{item.caseId}, #{item.reviewStatus}, " +
            "#{item.userId}, #{item.createTime}, #{item.updateTime})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<Evidence> list);
}
