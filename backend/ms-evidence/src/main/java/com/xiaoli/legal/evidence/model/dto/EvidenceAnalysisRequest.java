package com.xiaoli.legal.evidence.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 证据分析请求
 */
public class EvidenceAnalysisRequest {

    /**
     * 证据列表
     */
    @NotNull(message = "证据列表不能为空")
    private List<EvidenceItem> evidences;

    /**
     * 案件ID
     */
    private Long caseId;

    /**
     * 我方立场
     */
    private String ourPosition;

    /**
     * 证明目的
     */
    private String proofPurpose;

    // Getters and Setters
    public List<EvidenceItem> getEvidences() { return evidences; }
    public void setEvidences(List<EvidenceItem> evidences) { this.evidences = evidences; }

    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }

    public String getOurPosition() { return ourPosition; }
    public void setOurPosition(String ourPosition) { this.ourPosition = ourPosition; }

    public String getProofPurpose() { return proofPurpose; }
    public void setProofPurpose(String proofPurpose) { this.proofPurpose = proofPurpose; }

    /**
     * 证据项
     */
    public static class EvidenceItem {
        /**
         * 证据名称
         */
        @NotBlank(message = "证据名称不能为空")
        private String name;

        /**
         * 证据类型
         */
        @NotBlank(message = "证据类型不能为空")
        private String type;

        /**
         * 证据来源
         */
        private String source;

        /**
         * 证明目的
         */
        private String purpose;

        /**
         * 证据内容
         */
        private String content;

        /**
         * 证据页数/份数
         */
        private Integer pages;

        /**
         * 备注
         */
        private String remark;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }

        public String getPurpose() { return purpose; }
        public void setPurpose(String purpose) { this.purpose = purpose; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public Integer getPages() { return pages; }
        public void setPages(Integer pages) { this.pages = pages; }

        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
    }
}
