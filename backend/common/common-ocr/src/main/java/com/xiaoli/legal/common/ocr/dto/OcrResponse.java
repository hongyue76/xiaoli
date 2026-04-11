package com.xiaoli.legal.common.ocr.dto;

import java.util.List;

/**
 * OCR识别响应
 */
public class OcrResponse {

    /**
     * 识别的文本
     */
    private String text;

    /**
     * 文本块列表
     */
    private List<TextBlock> blocks;

    /**
     * 置信度(0-100)
     */
    private Double confidence;

    /**
     * 处理时间(毫秒)
     */
    private Long processTime;

    // Getters and Setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public List<TextBlock> getBlocks() { return blocks; }
    public void setBlocks(List<TextBlock> blocks) { this.blocks = blocks; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public Long getProcessTime() { return processTime; }
    public void setProcessTime(Long processTime) { this.processTime = processTime; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String text;
        private List<TextBlock> blocks;
        private Double confidence;
        private Long processTime;

        public Builder text(String text) { this.text = text; return this; }
        public Builder blocks(List<TextBlock> blocks) { this.blocks = blocks; return this; }
        public Builder confidence(Double confidence) { this.confidence = confidence; return this; }
        public Builder processTime(Long processTime) { this.processTime = processTime; return this; }

        public OcrResponse build() {
            OcrResponse response = new OcrResponse();
            response.setText(text);
            response.setBlocks(blocks);
            response.setConfidence(confidence);
            response.setProcessTime(processTime);
            return response;
        }
    }

    /**
     * 文本块
     */
    public static class TextBlock {
        /**
         * 文本内容
         */
        private String text;

        /**
         * 置信度
         */
        private Double confidence;

        /**
         * X坐标
         */
        private Integer x;

        /**
         * Y坐标
         */
        private Integer y;

        /**
         * 宽度
         */
        private Integer width;

        /**
         * 高度
         */
        private Integer height;

        // Getters and Setters
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }

        public Double getConfidence() { return confidence; }
        public void setConfidence(Double confidence) { this.confidence = confidence; }

        public Integer getX() { return x; }
        public void setX(Integer x) { this.x = x; }

        public Integer getY() { return y; }
        public void setY(Integer y) { this.y = y; }

        public Integer getWidth() { return width; }
        public void setWidth(Integer width) { this.width = width; }

        public Integer getHeight() { return height; }
        public void setHeight(Integer height) { this.height = height; }

        // Builder
        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String text;
            private Double confidence;
            private Integer x;
            private Integer y;
            private Integer width;
            private Integer height;

            public Builder text(String text) { this.text = text; return this; }
            public Builder confidence(Double confidence) { this.confidence = confidence; return this; }
            public Builder x(Integer x) { this.x = x; return this; }
            public Builder y(Integer y) { this.y = y; return this; }
            public Builder width(Integer width) { this.width = width; return this; }
            public Builder height(Integer height) { this.height = height; return this; }

            public TextBlock build() {
                TextBlock block = new TextBlock();
                block.setText(text);
                block.setConfidence(confidence);
                block.setX(x);
                block.setY(y);
                block.setWidth(width);
                block.setHeight(height);
                return block;
            }
        }
    }
}
