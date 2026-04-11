package com.xiaoli.legal.common.ai.dto;

/**
 * ASR请求
 */
public class AsrRequest {

    /**
     * 模型
     */
    private String model;

    /**
     * 音频格式: wav, mp3, pcm, flac, ogg
     */
    private String format = "wav";

    /**
     * 采样率 8000, 16000
     */
    private Integer sampleRate = 16000;

    /**
     * 音频语言: zh-CN, en-US
     */
    private String language = "zh-CN";

    /**
     * 是否启用流体
     */
    private Boolean stream = false;

    // Getters and Setters
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public Integer getSampleRate() { return sampleRate; }
    public void setSampleRate(Integer sampleRate) { this.sampleRate = sampleRate; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public Boolean getStream() { return stream; }
    public void setStream(Boolean stream) { this.stream = stream; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String model;
        private String format = "wav";
        private Integer sampleRate = 16000;
        private String language = "zh-CN";
        private Boolean stream = false;

        public Builder model(String model) { this.model = model; return this; }
        public Builder format(String format) { this.format = format; return this; }
        public Builder sampleRate(Integer sampleRate) { this.sampleRate = sampleRate; return this; }
        public Builder language(String language) { this.language = language; return this; }
        public Builder stream(Boolean stream) { this.stream = stream; return this; }

        public AsrRequest build() {
            AsrRequest req = new AsrRequest();
            req.model = this.model;
            req.format = this.format;
            req.sampleRate = this.sampleRate;
            req.language = this.language;
            req.stream = this.stream;
            return req;
        }
    }
}
