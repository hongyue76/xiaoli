package com.xiaoli.legal.evidence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaoli.legal.evidence.model.entity.EvidenceAnalysis;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 证据分析结果Mapper
 */
@Mapper
public interface EvidenceAnalysisMapper extends BaseMapper<EvidenceAnalysis> {

    /**
     * 批量插入证据分析结果
     */
    @Insert("<script>" +
            "INSERT INTO evidence_analysis (evidence_id, authenticity_analysis, legality_analysis, relevance_analysis, " +
            "probative_value, probative_score, issues, cross_examination, reinforcement, create_time) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.evidenceId}, #{item.authenticityAnalysis}, #{item.legalityAnalysis}, #{item.relevanceAnalysis}, " +
            "#{item.probativeValue}, #{item.probativeScore}, #{item.issues}, #{item.crossExamination}, " +
            "#{item.reinforcement}, #{item.createTime})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<EvidenceAnalysis> list);
}
