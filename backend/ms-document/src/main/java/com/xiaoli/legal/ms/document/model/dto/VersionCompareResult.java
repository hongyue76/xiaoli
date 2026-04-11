package com.xiaoli.legal.ms.document.model.dto;

import java.util.List;

/**
 * 版本比对结果
 */
public class VersionCompareResult {

    /**
     * 文档ID
     */
    private Long documentId;

    /**
     * 旧版本号
     */
    private Integer oldVersion;

    /**
     * 新版本号
     */
    private Integer newVersion;

    /**
     * 差异类型：ADDED-新增, DELETED-删除, MODIFIED-修改
     */
    private String diffType;

    /**
     * 差异摘要
     */
    private String summary;

    /**
     * 详细差异列表
     */
    private List<DiffLine> diffLines;

    /**
     * 统计信息
     */
    private DiffStats stats;

    // Getters and Setters
    public Long getDocumentId() { return documentId; }
    public void setDocumentId(Long documentId) { this.documentId = documentId; }

    public Integer getOldVersion() { return oldVersion; }
    public void setOldVersion(Integer oldVersion) { this.oldVersion = oldVersion; }

    public Integer getNewVersion() { return newVersion; }
    public void setNewVersion(Integer newVersion) { this.newVersion = newVersion; }

    public String getDiffType() { return diffType; }
    public void setDiffType(String diffType) { this.diffType = diffType; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<DiffLine> getDiffLines() { return diffLines; }
    public void setDiffLines(List<DiffLine> diffLines) { this.diffLines = diffLines; }

    public DiffStats getStats() { return stats; }
    public void setStats(DiffStats stats) { this.stats = stats; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long documentId;
        private Integer oldVersion;
        private Integer newVersion;
        private String diffType;
        private String summary;
        private List<DiffLine> diffLines;
        private DiffStats stats;

        public Builder documentId(Long documentId) { this.documentId = documentId; return this; }
        public Builder oldVersion(Integer oldVersion) { this.oldVersion = oldVersion; return this; }
        public Builder newVersion(Integer newVersion) { this.newVersion = newVersion; return this; }
        public Builder diffType(String diffType) { this.diffType = diffType; return this; }
        public Builder summary(String summary) { this.summary = summary; return this; }
        public Builder diffLines(List<DiffLine> diffLines) { this.diffLines = diffLines; return this; }
        public Builder stats(DiffStats stats) { this.stats = stats; return this; }

        public VersionCompareResult build() {
            VersionCompareResult result = new VersionCompareResult();
            result.setDocumentId(documentId);
            result.setOldVersion(oldVersion);
            result.setNewVersion(newVersion);
            result.setDiffType(diffType);
            result.setSummary(summary);
            result.setDiffLines(diffLines);
            result.setStats(stats);
            return result;
        }
    }

    /**
     * 差异行
     */
    public static class DiffLine {
        /**
         * 行号（旧版本）
         */
        private Integer oldLineNo;
        /**
         * 行号（新版本）
         */
        private Integer newLineNo;
        /**
         * 行内容（旧版本）
         */
        private String oldContent;
        /**
         * 行内容（新版本）
         */
        private String newContent;
        /**
         * 差异类型：ADD, DELETE, CONTEXT
         */
        private String type;

        public DiffLine() {}

        public DiffLine(Integer oldLineNo, Integer newLineNo, String oldContent, String newContent, String type) {
            this.oldLineNo = oldLineNo;
            this.newLineNo = newLineNo;
            this.oldContent = oldContent;
            this.newContent = newContent;
            this.type = type;
        }

        // Getters and Setters
        public Integer getOldLineNo() { return oldLineNo; }
        public void setOldLineNo(Integer oldLineNo) { this.oldLineNo = oldLineNo; }

        public Integer getNewLineNo() { return newLineNo; }
        public void setNewLineNo(Integer newLineNo) { this.newLineNo = newLineNo; }

        public String getOldContent() { return oldContent; }
        public void setOldContent(String oldContent) { this.oldContent = oldContent; }

        public String getNewContent() { return newContent; }
        public void setNewContent(String newContent) { this.newContent = newContent; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        // Builder
        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private Integer oldLineNo;
            private Integer newLineNo;
            private String oldContent;
            private String newContent;
            private String type;

            public Builder oldLineNo(Integer oldLineNo) { this.oldLineNo = oldLineNo; return this; }
            public Builder newLineNo(Integer newLineNo) { this.newLineNo = newLineNo; return this; }
            public Builder oldContent(String oldContent) { this.oldContent = oldContent; return this; }
            public Builder newContent(String newContent) { this.newContent = newContent; return this; }
            public Builder type(String type) { this.type = type; return this; }

            public DiffLine build() {
                return new DiffLine(oldLineNo, newLineNo, oldContent, newContent, type);
            }
        }
    }

    /**
     * 统计信息
     */
    public static class DiffStats {
        /**
         * 新增行数
         */
        private int addedLines;
        /**
         * 删除行数
         */
        private int deletedLines;
        /**
         * 修改行数
         */
        private int modifiedLines;
        /**
         * 保持不变行数
         */
        private int unchangedLines;

        public DiffStats() {}

        public DiffStats(int addedLines, int deletedLines, int modifiedLines, int unchangedLines) {
            this.addedLines = addedLines;
            this.deletedLines = deletedLines;
            this.modifiedLines = modifiedLines;
            this.unchangedLines = unchangedLines;
        }

        // Getters and Setters
        public int getAddedLines() { return addedLines; }
        public void setAddedLines(int addedLines) { this.addedLines = addedLines; }

        public int getDeletedLines() { return deletedLines; }
        public void setDeletedLines(int deletedLines) { this.deletedLines = deletedLines; }

        public int getModifiedLines() { return modifiedLines; }
        public void setModifiedLines(int modifiedLines) { this.modifiedLines = modifiedLines; }

        public int getUnchangedLines() { return unchangedLines; }
        public void setUnchangedLines(int unchangedLines) { this.unchangedLines = unchangedLines; }

        // Builder
        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private int addedLines;
            private int deletedLines;
            private int modifiedLines;
            private int unchangedLines;

            public Builder addedLines(int addedLines) { this.addedLines = addedLines; return this; }
            public Builder deletedLines(int deletedLines) { this.deletedLines = deletedLines; return this; }
            public Builder modifiedLines(int modifiedLines) { this.modifiedLines = modifiedLines; return this; }
            public Builder unchangedLines(int unchangedLines) { this.unchangedLines = unchangedLines; return this; }

            public DiffStats build() {
                return new DiffStats(addedLines, deletedLines, modifiedLines, unchangedLines);
            }
        }
    }
}
