package com.xiaoli.legal.ms.caseinfo.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 法律案例
 */
@TableName("legal_case")
public class LegalCase {

    /**
     * 案例ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 案例标题
     */
    private String title;

    /**
     * 案例编号
     */
    private String caseNo;

    /**
     * 案件类型
     */
    private String caseType;

    /**
     * 案由
     */
    private String cause;

    /**
     * 审理法院
     */
    private String court;

    /**
     * 法官
     */
    private String judge;

    /**
     * 裁判日期
     */
    private LocalDate judgmentDate;

    /**
     * 案件状态: FIRST_INSTANCE-一审, SECOND_INSTANCE-二审, FINAL-终审, RETRIAL-再审
     */
    private String caseStatus;

    /**
     * 当事人信息(JSON)
     */
    private String parties;

    /**
     * 案情简介
     */
    private String summary;

    /**
     * 争议焦点
     */
    private String disputeFocus;

    /**
     * 裁判要旨
     */
    private String rulingIdea;

    /**
     * 裁判结果
     */
    private String judgmentResult;

    /**
     * 法律依据
     */
    private String legalBasis;

    /**
     * 完整判决书内容
     */
    private String content;

    /**
     * 标签(JSON数组)
     */
    private String tags;

    /**
     * 向量ID(Milvus)
     */
    private String vectorId;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 来源
     */
    private String source;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCaseNo() { return caseNo; }
    public void setCaseNo(String caseNo) { this.caseNo = caseNo; }

    public String getCaseType() { return caseType; }
    public void setCaseType(String caseType) { this.caseType = caseType; }

    public String getCause() { return cause; }
    public void setCause(String cause) { this.cause = cause; }

    public String getCourt() { return court; }
    public void setCourt(String court) { this.court = court; }

    public String getJudge() { return judge; }
    public void setJudge(String judge) { this.judge = judge; }

    public LocalDate getJudgmentDate() { return judgmentDate; }
    public void setJudgmentDate(LocalDate judgmentDate) { this.judgmentDate = judgmentDate; }

    public String getCaseStatus() { return caseStatus; }
    public void setCaseStatus(String caseStatus) { this.caseStatus = caseStatus; }

    public String getParties() { return parties; }
    public void setParties(String parties) { this.parties = parties; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getDisputeFocus() { return disputeFocus; }
    public void setDisputeFocus(String disputeFocus) { this.disputeFocus = disputeFocus; }

    public String getRulingIdea() { return rulingIdea; }
    public void setRulingIdea(String rulingIdea) { this.rulingIdea = rulingIdea; }

    public String getJudgmentResult() { return judgmentResult; }
    public void setJudgmentResult(String judgmentResult) { this.judgmentResult = judgmentResult; }

    public String getLegalBasis() { return legalBasis; }
    public void setLegalBasis(String legalBasis) { this.legalBasis = legalBasis; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getVectorId() { return vectorId; }
    public void setVectorId(String vectorId) { this.vectorId = vectorId; }

    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
