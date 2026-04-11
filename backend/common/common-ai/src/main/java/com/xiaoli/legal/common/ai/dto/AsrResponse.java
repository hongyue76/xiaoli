package com.xiaoli.legal.common.ai.dto;

/**
 * ASR响应
 */
public class AsrResponse {

    /**
     * 识别文本
     */
    private String text;

    /**
     * 置信度(0-1)
     */
    private Double confidence;

    /**
     * 是否结束
     */
    private Boolean done;

    /**
     * 错误信息
     */
    private String error;

    // Getters and Setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public Boolean getDone() { return done; }
    public void setDone(Boolean done) { this.done = done; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String text;
        private Double confidence;
        private Boolean done;
        private String error;

        public Builder text(String text) { this.text = text; return this; }
        public Builder confidence(Double confidence) { this.confidence = confidence; return this; }
        public Builder done(Boolean done) { this.done = done; return this; }
        public Builder error(String error) { this.error = error; return this; }

        public AsrResponse build() {
            AsrResponse resp = new AsrResponse();
            resp.text = this.text;
            resp.confidence = this.confidence;
            resp.done = this.done;
            resp.error = this.error;
            return resp;
        }
    }
}
