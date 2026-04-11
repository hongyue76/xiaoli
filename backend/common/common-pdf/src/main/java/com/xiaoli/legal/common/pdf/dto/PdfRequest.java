package com.xiaoli.legal.common.pdf.dto;

import java.util.Map;

/**
 * PDF生成请求
 */
public class PdfRequest {

    private String title;
    private String content;
    private String author;
    private String pageSize = "A4";
    private String orientation = "portrait";
    private Integer margin = 20;
    private Integer fontSize = 12;
    private Integer lineHeight = 20;
    private Map<String, Object> data;

    public PdfRequest() {}

    public PdfRequest(String title, String content, String author, String pageSize, String orientation,
                     Integer margin, Integer fontSize, Integer lineHeight, Map<String, Object> data) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.pageSize = pageSize;
        this.orientation = orientation;
        this.margin = margin;
        this.fontSize = fontSize;
        this.lineHeight = lineHeight;
        this.data = data;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String title;
        private String content;
        private String author;
        private String pageSize = "A4";
        private String orientation = "portrait";
        private Integer margin = 20;
        private Integer fontSize = 12;
        private Integer lineHeight = 20;
        private Map<String, Object> data;

        public Builder title(String title) { this.title = title; return this; }
        public Builder content(String content) { this.content = content; return this; }
        public Builder author(String author) { this.author = author; return this; }
        public Builder pageSize(String pageSize) { this.pageSize = pageSize; return this; }
        public Builder orientation(String orientation) { this.orientation = orientation; return this; }
        public Builder margin(Integer margin) { this.margin = margin; return this; }
        public Builder fontSize(Integer fontSize) { this.fontSize = fontSize; return this; }
        public Builder lineHeight(Integer lineHeight) { this.lineHeight = lineHeight; return this; }
        public Builder data(Map<String, Object> data) { this.data = data; return this; }

        public PdfRequest build() {
            return new PdfRequest(title, content, author, pageSize, orientation, margin, fontSize, lineHeight, data);
        }
    }

    // Getters
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }
    public String getPageSize() { return pageSize; }
    public String getOrientation() { return orientation; }
    public Integer getMargin() { return margin; }
    public Integer getFontSize() { return fontSize; }
    public Integer getLineHeight() { return lineHeight; }
    public Map<String, Object> getData() { return data; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setAuthor(String author) { this.author = author; }
    public void setPageSize(String pageSize) { this.pageSize = pageSize; }
    public void setOrientation(String orientation) { this.orientation = orientation; }
    public void setMargin(Integer margin) { this.margin = margin; }
    public void setFontSize(Integer fontSize) { this.fontSize = fontSize; }
    public void setLineHeight(Integer lineHeight) { this.lineHeight = lineHeight; }
    public void setData(Map<String, Object> data) { this.data = data; }
}
